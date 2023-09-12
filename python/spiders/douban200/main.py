from scrapy import cmdline

if __name__ == '__main__':
    #--nolog
    cmdline.execute("scrapy crawl douban ".split())
