# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class BeikeTestItem(scrapy.Item):
    # define the fields for your item here like:
    id = scrapy.Field()
    total_price = scrapy.Field()
    room_info_str = scrapy.Field()
    room_layout = scrapy.Field()
    room_area = scrapy.Field()
    gua_pai_date = scrapy.Field()
    transaction_date = scrapy.Field()
    room_age = scrapy.Field()
    cell_name = scrapy.Field()
    area_name = scrapy.Field()
    street_name = scrapy.Field()
    href = scrapy.Field()
