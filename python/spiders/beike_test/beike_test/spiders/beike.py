import scrapy
from bs4 import BeautifulSoup
from scrapy import Request

from spiders.beike_test.beike_test.items import BeikeTestItem


class BeikeSpider(scrapy.Spider):
    name = "beike"
    allowed_domains = ["tj.ke.com"]
    start_urls = ["https://tj.ke.com/ershoufang/nankai/"]

    def parse(self, response):
        li_list = response.css('#beike > div.sellListPage > div.content > div.leftContent > div:nth-child(4) > ul')
        for li in li_list:
            item=BeikeTestItem()
            href = li.css('ul > li:nth-child(1) > div > div.title > a::attr(href)').extract_first()
            item["href"] =href
            yield Request(
                url=href,
                callback=self.parse_detail,
                cb_kwargs={'item': item}
            )

    def parse_detail(self, response, **kwargs):
        item = kwargs['item']
        html_doc = response.body
        # html_doc = html_doc.decode('utf-8')
        soup = BeautifulSoup(html_doc, 'lxml')
        price_container=soup.select_one("price-container")

        yield item
