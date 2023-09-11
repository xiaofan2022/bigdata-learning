import scrapy
from cssselect import Selector

from spiders.douban200.douban200.items import DoubanItem


class DoubanSpider(scrapy.Spider):
    name = "douban"
    allowed_domains = ["movie.douban.com"]
    start_urls = ["https://movie.douban.com/top250"]

    def parse(self, response):
        li_list=response.css("#content > div > div.article > ol > li")
        for li in li_list:
            item=DoubanItem()
            # content > div > div.article > ol > li:nth-child(1) > div > div.info > div.hd > a > span:nth-child(1)
            item["title"]=li.css("span.title::text").extract_first()
            item["rank"]=li.css("div > div.pic > em::text").extract_first()
            item["subject"]=li.css('span.inq::text').extract_first()
            yield item
