import logging
import time
import random
from multiprocessing import Process

from scrapy import cmdline


def run_scrapy(param):
    print("{}开始跑".format(param[1]))
    # --nolog
    cmdline.execute(
        "scrapy crawl beike -aarea_name={} -apages={}"
        " -astreet_name={} -aurl_street_name={}".format(param[0],
                                                        param[3],
                                                        param[1],
                                                        param[2]).split())
    print("{}执行完毕".format(param[1]))



if __name__ == '__main__':
    param_array = [
        ("北辰", "双口镇", "shuangkouzhen", 2),
        ("北辰", "天穆镇", "tianmuzhen", 43),
        ("北辰", "小淀镇", "xiaodianzhen", 11),
        ("北辰", "宜兴埠", "yixingbu", 64),
        ("武清", "保利金街", "baolijinjie", 13),
        ("武清", "大王古镇", "daiwangguzhen", 5),
        ("武清", "佛罗伦萨", "fuluolunsa", 16),
        ("武清", "高村", "gaocun", 32),
        ("武清", "河西务", "hexiwu", 2),
        ("武清", "黄庄", "huangzhuang", 48),
        ("武清", "静湖", "jinghu", 40),
        ("武清", "南湖", "nanhu6", 45),
        ("武清", "泗村", "sicun", 9),
        ("武清", "体育中心", "tiyuzhongxin2", 25),
        ("武清", "武清其它", "wuqingqita", 100),
        ("武清", "下朱庄", "xiazhuzhuang", 70),

       

    ]
    #cmdline.execute("scrapy crawl beike".split())
    process_list = []
    for i in range(len(param_array)):
        p = Process(target=run_scrapy, args=(param_array[i],))
        process_list.append(p)
    for p in process_list:
        p.start()
        t = random.randint(2, 5)  # 随机一个大于等于1且小于等于5的整数
        time.sleep(t)
        p.join()  # 等待当前进程完成后再启动下一个进程
