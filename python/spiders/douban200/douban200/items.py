# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class DoubanMovieTop250Item(scrapy.Item):
    name = scrapy.Field()
    pic_link = scrapy.Field()
    rank = scrapy.Field()
    director_actor = scrapy.Field()
    info = scrapy.Field()
    rating_score = scrapy.Field()
    rating_num = scrapy.Field()
    introduce = scrapy.Field()
