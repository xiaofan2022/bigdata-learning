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
        sql = """
            INSERT INTO second_house_info(bei_ke_id, total_price, bedroom_num, live_room_num, chao_xiang, floor_desc,
                                          total_floor_num, room_area, pub_date_str, cell_name, area_name,street_name, tags)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s, %s)
        """

        self.cursor.executemany(sql, [
            (item['bei_ke_id'], item['total_price'], item['bedroom_num'], item['live_room_num'], item['chao_xiang'],
             item['floor_desc'], item['total_floor_num'], item['room_area'], item['pub_date_str'], item['cell_name'],
             item['area_name'],item['street_name'], item['tags'])
            for item in self.data
        ])
        self.conn.commit()

    def close_spider(self, spider):
        if len(self.data)>0:
            self.batch_insert()

        self.conn.commit()
        # 关闭游标对象
        self.cursor.close()
        # 关闭数据库连接
        self.conn.close()