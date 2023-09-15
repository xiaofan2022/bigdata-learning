import re
from datetime import datetime

import scrapy
from scrapy import Request

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
        item['total_price'] = response.xpath("//span[@class='total']/text()").extract_first()
        # 2室1厅;北;61.85平米
        main_infos = response.xpath("//div[@class='mainInfo']/text()").extract()
        item['room_info_str'] = main_infos[0]
        item['room_layout'] = main_infos[1]
        item['room_area'] = re.findall(r'\d+\.\d+', main_infos[2])
        # 贝壳id
        item['id'] = \
        re.findall(r'\d+', response.xpath("//div[@class='houseRecord']//span[@class='info']/text()").extract_first())[0]
        gua_pai_date_str = response.xpath("//div[@class='transaction']//li[1]/text()").extract_first().replace(' ',
                                                                                                               '').replace(
            '\n', '')
        item['gua_pai_date'] = datetime.strptime(gua_pai_date_str, '%Y年%m月%d日')
        transaction_date_str = response.xpath("//div[@class='transaction']//li[3]/text()").extract_first().replace(' ',
                                                                                                                   '').replace(
            '\n', '')
        item['transaction_date'] = datetime.strptime(transaction_date_str, '%Y年%m月%d日')
        item['cell_name'] = response.xpath("//div[@class='communityName']//a[1]/text()").extract_first()
        item['area_name'] = response.xpath("//div[@class='areaName']//a[1]/text()").extract_first()
        item['street_name'] = response.xpath("//div[@class='areaName']//a[2]/text()").extract_first()
        item['room_age'] = response.xpath("//div[@class='transaction']//li[5]/text()").extract_first()
        yield item
