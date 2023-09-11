# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html
import pymysql
# useful for handling different item types with a single interface
from itemadapter import ItemAdapter
from twisted.enterprise import adbapi


class Douban200Pipeline:

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
        self.data=[]
    def process_item(self, item, spider):
        title=item.get("title","")
        rank=item.get("rank","")
        subject=item.get("subject","")

        # SQL 语句：插入数据
        self.data.append([title,rank,subject])
        if len(self.data)==100:
            self.batch_insert()
            self.data.clear()

    def batch_insert(self):
        self.cursor.executemany("""
          INSERT INTO douban_video_item( title, `rank`,`subject_str`) VALUES ( %s,%s,%s)
          """, self.data)
        self.conn.commit()

    def close_spider(self, spider):
        if len(self.data)>0:
            self.batch_insert()

        self.conn.commit()
        # 关闭游标对象
        self.cursor.close()
        # 关闭数据库连接
        self.conn.close()

