package com.sakha.services.datasourceanalyzer;

import com.sakha.services.pojo.Association;
import com.sakha.services.pojo.AssociationsData;
import com.sakha.services.util.ActiveMQUtil;
import com.sakha.services.util.ElasticSearchUtil;
import com.sakha.services.util.RestApiUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PPPProfileAnalysisImpl implements ProfileAnalysis{

	static final Logger log = Logger.getLogger(PPPProfileAnalysisImpl.class);
	@Autowired
	private ElasticSearchUtil elasticsearch;
	@Autowired
	private RestApiUtil restApiClient;
	@Autowired
	private ActiveMQUtil queueUtil;
	@Value("${PPP_DB}")
	private String elasticSearchIndex;
	@Value("${PPP_DATA_COLL}")
	private String elasticSearchType;
	@Value("${PPP_ES_DATAQUEUE}")
	private String esDataQueue;
	@Value("${PPP_MONGO_DATAQUEUE}")
	private String mongoDataQueue;


	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void extractUniversity() {
		log.info("Begin PPP-UE");
		int totalComputedRecordCount = 0;
		long hourlyComputedRecordCount = 0;
		int recordsScrolled = 0;
		int recordswithAssociationsField=0;

		Date date = new Date();
		int startHour= date.getHours();
		int lastHour= date.getHours();

		SearchResponse scrollResp = null;
		try {

			// get the results from the query for the source
			// 100 hits per shard will be returned for each scroll
			scrollResp = elasticsearch.getClient()
					.prepareSearch(elasticSearchIndex)
					.setTypes(elasticSearchType).setSearchType(SearchType.SCAN)
					.setScroll(new TimeValue(30000)).setSize(100).execute().actionGet();

			while (true) {

				scrollResp = elasticsearch.getClient()
						.prepareSearchScroll(scrollResp.getScrollId())
						.setScroll(new TimeValue(30000)).execute()
						.actionGet();

				// Break condition: No hits are returned
				if (scrollResp.getHits().getHits().length == 0) {
					log.info("PPP-UE : University extractor completed for all records ");
					break;
				}

				totalComputedRecordCount = totalComputedRecordCount + scrollResp.getHits().getHits().length;
				recordsScrolled = recordsScrolled + scrollResp.getHits().getHits().length;

				for (SearchHit hit : scrollResp.getHits().getHits()) {
					Map<String, Object> record = hit.getSource();
					String _id = hit.getId();

					boolean hasAssosiaction=false;
					if(record.containsKey("associations")
							&& ((List<String>) record.get("associations")).size()>0 ){
						recordswithAssociationsField++;
						hasAssosiaction=true;
					}

					//TODO Quick fix : need to handle this condition using filters
					/*if ( !hasAssosiaction || record.containsKey("newAcademicData") || record.containsKey("existingAcademicData")) {
						totalComputedRecordCount--;
						continue;
					}*/

					if ( !hasAssosiaction) {
						totalComputedRecordCount--;
						continue;
					}
					try
					{
						// Compute University Extraction
						record.put("_id", _id);
						String resultStr = restApiClient
								.getAssociation(prepareApiRequest(record));
						System.out.println("Record: " + resultStr);

						//Add resulting data for updation into both ES and Mongo queues
						queueUtil.addToQueue(esDataQueue, resultStr, _id);
						//queueUtil.addToQueue(mongoDataQueue, resultStr);

						hourlyComputedRecordCount++;

					}
					catch(Exception ex){
						totalComputedRecordCount--;
						log.error("PPP-UE : computation failed for record id : " +_id);
						log.error("Exception :" , ex );
					}
				}

				lastHour = new Date().getHours();
				if(startHour != lastHour)
				{
					startHour = lastHour;
					log.info("PPP-UE/Hour : scrolled records :"+recordsScrolled+" : records having assosiations : "+recordswithAssociationsField+" : Total Computed :"+ totalComputedRecordCount +" : per hour : "+ hourlyComputedRecordCount  );
					hourlyComputedRecordCount = 0;
				}

			}
		} catch (Exception ex) {
			log.error("Exception", ex);
		} finally {
			log.info("PPP-UE : scrolled records : "+ recordsScrolled +": records having assosiations : "+recordswithAssociationsField+" : Total computed: " + totalComputedRecordCount);
			elasticsearch.close();
		}
		log.info("END PPP-UE");
	}

	private AssociationsData prepareApiRequest(Map<String, Object> record) {

		AssociationsData associationsData = new AssociationsData();
		List<Association> associationList = new ArrayList<Association>();

		if (record.containsKey("associations")) {

			@SuppressWarnings("unchecked")
			List<String> associationsArr = (List<String>) record
					.get("associations");
			for (int i = 0; i < associationsArr.size(); i++) {

				Association association = new Association();
				association.setAssociation(associationsArr.get(i).toString().replaceAll("\"", ""));
				associationList.add(association);
			}
		}
		associationsData.setAssociationsData(associationList);

		return associationsData;
	}

	public void checkQuery(){
		SearchResponse scrollResp = null;

//		FilterBuilder filter= FilterBuilders.andFilter(
//				FilterBuilders.notFilter(FilterBuilders
//						.existsFilter("existingAcademicData")),
//				FilterBuilders.notFilter(FilterBuilders
//						.existsFilter("newAcademicData")),FilterBuilders.existsFilter("associations"));
//		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(query,
//				filter);

//		QueryBuilder filter = QueryBuilders.andQuery(QueryBuilders.notQuery(QueryBuilders.existsQuery("newAcademicData")))
//				.add(QueryBuilders.notQuery(QueryBuilders.existsQuery("existingAcademicData")))
//				.add(QueryBuilders.existsQuery("associations"));

		QueryBuilder filter = QueryBuilders.andQuery(QueryBuilders.missingQuery("existingAcademicData"))
				.add(QueryBuilders.notQuery(QueryBuilders.existsQuery("existingAcademicData")));

		FilteredQueryBuilder filterQuery = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),filter);
		// get the results from the query for the source
		// 100 hits per shard will be returned for each scroll
		scrollResp = elasticsearch.getClient()
				.prepareSearch(elasticSearchIndex)
				.setTypes(elasticSearchType).setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(30000)).setQuery(filterQuery).setSize(100).execute().actionGet();

		while (true) {

			scrollResp = elasticsearch.getClient()
					.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(30000)).execute()
					.actionGet();

			// Break condition: No hits are returned
			if (scrollResp.getHits().getHits().length == 0) {
				break;
			}

			for (SearchHit hit : scrollResp.getHits().getHits()) {
				Map<String, Object> record = hit.getSource();
				System.out.println(record.get("associations"));
			}
		}
	}

}
