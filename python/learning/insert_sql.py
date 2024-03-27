#-*-coding:utf-8-*-


# 读取文本文件
file_name = 'tables.txt'
table_name = ''
with open('D:\\tmp\\{}'.format(file_name), 'r',encoding='utf-8') as file:
    lines = file.readlines()

# 生成插入 SQL 语句
insert_values = []
for i in range(len(lines)):
    if i == 0:
        table_name = lines[0]
        continue
    values = lines[i].strip().split('\t')
    str=''
    str+='('
    for i  in range(len(values)):
        if i>0 and i<=3:
            str += "'{}'".format(values[i])+','
        else:
            str += values[i]+','

    insert_values.append(str[0:len(str)-1]+')')

# 将插入值拼接为 SQL 语句
sql = f"INSERT INTO {table_name.strip()} VALUES {', '.join(insert_values)};"
print(sql)
