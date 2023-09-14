# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
from itemadapter import ItemAdapter
import pymysql


class BeikeTestPipeline:

    def __init__(self):
        self.conn = pymysql.connect(
            host='hadoop101',
            port=3306,
            user='root',
            password='123456',
            database='test',
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        # 创建一个游标对象
        self.cursor = self.conn.cursor()
        self.data = []
    def process_item(self, item, spider):
        # SQL 语句：插入数据
        self.data.append(item)
        if len(self.data) == 100:
            self.batch_insert()
            self.data.clear()

    def batch_insert(self):
        sql= """
            INSERT INTO second_house_info(id,total_price, href, room_info_str, room_layout, room_area, gua_pai_date,
                                          transaction_date, room_age, cell_name, area_name, street_name)
            VALUES (%s ,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        self.cursor.executemany(sql,
                           [(item['id'],item['total_price'], item['href'], item['room_info_str'], item['room_layout'],
                             item['room_area'], item['gua_pai_date'], item['transaction_date'], item['room_age'],
                             item['cell_name'], item['area_name'], item['street_name']) for item in self.data])
        self.conn.commit()

    def close_spider(self, spider):
        if len(self.data)>0:
            self.batch_insert()

        self.conn.commit()
        # 关闭游标对象
        self.cursor.close()
        # 关闭数据库连接
        self.conn.close()