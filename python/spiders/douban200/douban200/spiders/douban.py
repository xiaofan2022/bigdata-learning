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
            item["title"]=li.css("span.title::text").extract_first()
            item["rank"]=li.css("span.rating_num::text").extract_first()
            item["subject"]=li.css('span.inq::text').extract_first()
            yield item
