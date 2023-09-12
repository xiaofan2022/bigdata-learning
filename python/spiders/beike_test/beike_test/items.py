# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class BeikeTestItem(scrapy.Item):
    # define the fields for your item here like:
    #小区名
    cell_name = scrapy.Field()
    home_id = scrapy.Field()
    href=scrapy.Field()
