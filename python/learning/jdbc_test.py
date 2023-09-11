import pymysql
import logging
logging.getLogger().setLevel(logging.INFO)

data = []
for i in range(1000):
    data.append((str(1 + i), str(2 + i),'haha'))  # 每条记录对应的是一个tuple，其实我试过list也是可以的
insert_cmd = "INSERT INTO douban_video_item( title, `rank`,subject_str) VALUES ( %s,%s,%s)"
#update_cmd = "update table set column1=%s, column2=%s where column3=%s"

con = pymysql.connect(host="hadoop101", port=3306, user="root", password="123456", db="test", charset="utf8mb4")
cur = con.cursor()

try:
    cur.executemany(insert_cmd, data)  # 批量插入的用例
    con.commit()

    #cur.executemany(update_cmd, data)  # 批量更新的用例
except Exception as e:
    logging.info(e)