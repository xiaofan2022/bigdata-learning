# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class BeikeTestItem(scrapy.Item):
    # define the fields for your item here like:
    bei_ke_id = scrapy.Field()
    total_price = scrapy.Field()
    bedroom_num = scrapy.Field()
    live_room_num = scrapy.Field()
    chao_xiang = scrapy.Field()
    floor_desc = scrapy.Field()
    total_floor_num = scrapy.Field()
    room_area = scrapy.Field()
    pub_date_str = scrapy.Field()
    cell_name = scrapy.Field()
    area_name = scrapy.Field()
    street_name = scrapy.Field()
    tags = scrapy.Field()
