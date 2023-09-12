import scrapy
from cssselect import Selector
from scrapy import Request

from spiders.douban200.douban200.items import DoubanMovieTop250Item


class DoubanSpider(scrapy.Spider):
    name = "douban"
    allowed_domains = ["movie.douban.com"]
    start_urls = ["https://movie.douban.com/top250"]

    def start_requests(self):
        for i in range(10):
            url = f'https://movie.douban.com/top250?start={25 * i}&filter='
            yield Request(url=url, callback=self.parse)
    def parse(self, response):
        li_list=response.css("#content > div > div.article > ol > li")
        for li in li_list:
            item=DoubanMovieTop250Item()
            # content > div > div.article > ol > li:nth-child(1) > div > div.info > div.hd > a > span:nth-child(1)
            item["title"]=li.css("span.title::text").extract_first()
            item["rank"]=li.css("div > div.pic > em::text").extract_first()
            item["subject"]=li.css('span.inq::text').extract_first()
            yield item
