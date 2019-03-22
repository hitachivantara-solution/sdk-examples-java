package com.hitachivantara.example.hcp.content;

import java.io.IOException;
import java.util.List;

import com.hitachivantara.common.ex.HSCException;
import com.hitachivantara.common.tools.PrettyRecordPrinter;
import com.hitachivantara.common.util.DatetimeFormat;
import com.hitachivantara.example.hcp.util.HCPClients;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.query.define.ObjectProperty;
import com.hitachivantara.hcp.query.define.Order;
import com.hitachivantara.hcp.query.model.ObjectQueryResult;
import com.hitachivantara.hcp.query.model.ObjectSummary;
import com.hitachivantara.hcp.query.model.QueryResult;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.standard.api.HCPNamespace;

/**
 * 基于 HCP MEQ的元数据基本搜索功能演示
 * 
 * @author sohan
 *
 */
public class QueryExample_BasedQuery {
	int i = 0;

	private HCPNamespace hcpClient;
	private HCPQuery hcpQuery;
	private final PrettyRecordPrinter printer = new PrettyRecordPrinter();

	public static void main(String[] args) throws IOException {
		QueryExample_BasedQuery example = new QueryExample_BasedQuery();

		try {
			example.init();

			// 执行查询示例
			example.query();
		} catch (InvalidResponseException e) {
			e.printStackTrace();
			return;
		} catch (HSCException e) {
			e.printStackTrace();
			return;
		}
	}

	public void init() throws HSCException {
		// 得到搜索客户端示例
		hcpQuery = HCPClients.getInstance().getHCPQueryClient();
		// 得到HCP客户端示例
		hcpClient = HCPClients.getInstance().getHCPClient();
	}

	public void query() throws InvalidResponseException, HSCException, IOException {

		// 创建搜索请求
		ObjectBasedQueryRequest request = new ObjectBasedQueryRequest();

		// 设置搜索语法 更多搜索语法可以参考《HCP搜索接口介绍.doc》

		// 搜索key包含abcdefg的文件
		 request.setQuery("+(objectPath:abcdefg)");
		// 搜索元数据中包含身份证110223201009028931的文件
		// request.setQuery("+(customMetadataContent:110223201009028931)");
		// 搜索元数据中包含male并且文件key包含beijing的文件
		// request.setQuery("+(customMetadataContent:male) +(objectPath:beijing)");

		// 设置结果排序，此处安装对象注入时间升序
		request.addSort(ObjectProperty.ingestTime, Order.asc);
		// request.addSort(ObjectProperty.size); //如未指明默认为 asc （升序）
		// 分页每页返回结果数量，此处设置100件
		request.setResults(100);
		// 全部结果的第n件开始返回
		// requestBody.setOffset(10);

		// 设置返回结果列，此处设置可忽略
		// 如未指定返回结果列信息，默认仅包含（changeTimeMilliseconds/key/name/urlName/versionId/operation）
		// request.addProperty(ObjectProperty.accessTime);
		// request.addProperty(ObjectProperty.accessTimeString);
		// request.addProperty(ObjectProperty.acl);
		// request.addProperty(ObjectProperty.aclGrant);
		// request.addProperty(ObjectProperty.changeTimeMilliseconds);
		// request.addProperty(ObjectProperty.changeTimeString);
		// request.addProperty(ObjectProperty.customMetadata);
		// request.addProperty(ObjectProperty.customMetadataAnnotation);
		// request.addProperty(ObjectProperty.dpl);
		// request.addProperty(ObjectProperty.gid);
		// request.addProperty(ObjectProperty.hash);
		// request.addProperty(ObjectProperty.hashScheme);
		// request.addProperty(ObjectProperty.hold);
		// request.addProperty(ObjectProperty.index);
		request.addProperty(ObjectProperty.ingestTime);
		// request.addProperty(ObjectProperty.ingestTimeString);
		// request.addProperty(ObjectProperty.namespace);
		request.addProperty(ObjectProperty.objectPath);
		// request.addProperty(ObjectProperty.operation);
		// request.addProperty(ObjectProperty.owner);
		// request.addProperty(ObjectProperty.permissions);
		// request.addProperty(ObjectProperty.replicated);
		// request.addProperty(ObjectProperty.replicationCollision);
		// request.addProperty(ObjectProperty.retention);
		// request.addProperty(ObjectProperty.retentionClass);
		// request.addProperty(ObjectProperty.retentionString);
		// request.addProperty(ObjectProperty.shred);
		request.addProperty(ObjectProperty.size);
		// request.addProperty(ObjectProperty.type);
		// request.addProperty(ObjectProperty.uid);
		// request.addProperty(ObjectProperty.updateTime);
		// request.addProperty(ObjectProperty.updateTimeString);
		request.addProperty(ObjectProperty.urlName);
		// request.addProperty(ObjectProperty.utf8Name);
		// request.addProperty(ObjectProperty.version);

		// 关键项目出现统计
		// request.addFacet(Facet.namespace);
		// request.addFacet(Facet.hold);
		// request.addFacet(Facet.retention);
		// request.addFacet(Facet.retentionClass);

		ObjectQueryResult result = null;
		// EXEC TEST FUNCTION ---------------------------------------------------------------------

		// 触发搜索
		result = hcpQuery.query(request);
		
		// 处理搜索结果
		handleResult(result);

		// 判断是否还有下一页
		while (result.isIncomplete()) {
			// System.out.println(request.getRequestBody().build());

			// long s = System.currentTimeMillis();
			// 如果还有下一页搜索Nextpage
			result = hcpQuery.query(request.withNextPage());

			// long e = System.currentTimeMillis();
			// System.out.println(e-s);

			// 处理搜索结果
			handleResult(result);
		}

		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
		 // 搜索上一页结果
//		 result = hcpQuery.query(request.withPrevPage());
//		
//		 // 处理搜索结果
//		 handleResult(result);
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	}

	private void handleResult(final QueryResult result) throws InvalidResponseException, HSCException, IOException {
		long totalFound = result.getStatus().getTotalResults();
		System.out.println("一共找到 "+totalFound+" 件匹配记录");

		List<ObjectSummary> res = result.getResults();
		for (ObjectSummary objectSummary : res) {
			// 在这里处理搜索到的结果，此处作为示例打印了结果
			printer.appendRecord(++i,
					objectSummary.getKey(),
					DatetimeFormat.YYYY_MM_DD_HHMMSS.format(objectSummary.getIngestTime()),
					objectSummary.getVersionId(),
					objectSummary.getUrlName());

			// 通过结果可以对对象进行自定义操作
//			HCPObject obj = hcpClient.getObject(objectSummary.getKey());
//			String content1 = StreamUtils.inputStreamToString(obj.getContent(), true);
//			// Do something...
//			System.out.println(content1);

//			String content2 = hcpClient.getObject(objectSummary.getKey(), new ObjectParser<String>() {
//
//				@Override
//				public String parse(HCPObject object) throws ParseException {
//					try {
//						return StreamUtils.inputStreamToString(object.getContent(), true);
//					} catch (IOException e) {
//						e.printStackTrace();
//						throw new ParseException(e);
//					}
//				}
//			});
//			// Do something...
//			System.out.println(content2);

		}

		printer.printout();
	}

}
