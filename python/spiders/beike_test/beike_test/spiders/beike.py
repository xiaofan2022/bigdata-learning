import scrapy
from bs4 import BeautifulSoup
from scrapy import Request
import  re
from spiders.beike_test.beike_test.items import BeikeTestItem


class BeikeSpider(scrapy.Spider):
    name = "beike"
    allowed_domains = ["tj.ke.com"]
    start_urls = ["https://tj.ke.com/ershoufang/nankai/"]

    def parse(self, response):
        li_list = response.xpath("//ul[@class='sellListContent']//li[@class='clear']")
        for li in li_list:
            item=BeikeTestItem()
            href = li.xpath("./a/@href").extract()[0]
            item["href"] =href
            yield Request(
                url=href,
                callback=self.parse_detail,
                cb_kwargs={'item': item}
            )

    def parse_detail(self, response, **kwargs):
        item = kwargs['item']
        # 总价
        item['total_price'] = response.xpath("//span[@class='total']/text()")
        # 2室1厅;北;61.85平米
        main_infos = response.xpath("//div[@class='mainInfo']/text()")
        item['room_info_str'] = main_infos[0]
        item['room_layout'] = main_infos[1]
        item['room_area'] = re.findall(r'\d+\.\d+', main_infos[2])
        # 贝壳id
        item['id'] = re.findall(r'\d+', response.xpath("//div[@class='houseRecord']//span[@class='info']/text()")[0])
        item['gua_pai_date'] =response.xpath("//div[@class='transaction']//li[1]/text()")
        item['transaction_date'] =response.xpath("//div[@class='transaction']//li[3]/text()")
        item['cell_name'] = response.xpath("//div[@class='communityName']//a[1]/text()")
        item['area_name'] = response.xpath("//div[@class='areaName']//a[1]/text()")
        item['street_name'] = response.xpath("//div[@class='areaName']//a[2]/text()")
        item['room_age'] = response.xpath("//div[@class='transaction']//li[5]/text()")
        yield item
