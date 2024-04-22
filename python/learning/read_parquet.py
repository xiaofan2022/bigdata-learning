import pyarrow.parquet as pq

if __name__ == '__main__':
    # 打开Parquet文件
    parquet_file = pq.ParquetFile(
        'D:\data\warehouse\hudi\hudi_constomer_no_partition_spark\\87fea34f-7e5e-4d52-acd8-1fdecef5eda3-0_7-15-0_20240415163732228.parquet')

    # 读取整个文件内容
    table = parquet_file.read()

    # 获取列数据
    # column_data = table.column('column_name')
    # print(column_data)

    # 获取列名称
    for i in range(parquet_file.num_row_groups):
        row_group = parquet_file.read_row_group(i)
        for j in range(row_group.num_rows):
            row = row_group[j]
            column_data1 = table.column('customer_name')
            print(column_data1)
            # 获取列数据
            column_data = table.column('test_time')
            print(column_data)
            column_data3 = table.column('insert_date')
            print(column_data3)

    #
    # # 获取列类型
    column_types = table.schema.types
    print(column_types)

