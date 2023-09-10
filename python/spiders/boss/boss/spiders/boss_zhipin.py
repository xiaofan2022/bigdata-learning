import scrapy

from spiders.boss.boss.items import BossItem


class BossZhipinSpider(scrapy.Spider):
    name = "boss_zhipin"
    allowed_domains = ["zhipin.com"]
    start_urls = ["https://www.zhipin.com/c101010100/?query=python&page=1&ka=page-1"]

    def parse(self, response):
        print("ssss")
        # item = BossItem()
        # # 获取页面数据的条数
        # nodeList = response.xpath('//div[@class="job-primary"]')
        # for node in nodeList:
        #     item["job_title"] = node.xpath('.//div[@class="job-title"]/text()').extract()[0]
        #     item["compensation"] = node.xpath('.//span[@class="red"]/text()').extract()[0]
        #     item["company"] = node.xpath('.//div[@class="info-company"]//h3//a/text()').extract()[0]
        #     company_info = node.xpath('.//div[@class="info-company"]//p/text()')
        #     temp = node.xpath('.//div[@class="info-primary"]//p/text()')
        #     item["address"] = temp[0]
        #     item["seniority"] = temp[1]
        #     item["education"] = temp[2]
        #     if len(company_info) < 3:
        #         item["company_type"] = company_info[0]
        #         item["company_finance"] = ""
        #         item["company_quorum"] = company_info[-1]
        #     else:
        #         item["company_type"] = company_info[0]
        #         item["company_finance"] = company_info[1]
        #         item["company_quorum"] = company_info[2]
