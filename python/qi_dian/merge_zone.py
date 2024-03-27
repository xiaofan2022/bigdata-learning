import csv


def load_csv(filename):
    data = []
    with open(filename, 'r', newline='', encoding='utf-8') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            data.append(row)
    return data


def merge_tables(table1, table2):
    merged_data = []
    for row1 in table1:
        for row2 in table2:
            if row1[1] == row2[1] and row1[2] == row2[2]:
                #merged_row = dict(zip(range(len(row1)), row1))  # 将列表转换为字典
                #merged_row.update(zip(range(len(row1), len(row1) + len(row2)), row2[2:]))
                merged_data.append((row1[0],row2[0],row2[1],row2[2]))
                break
    return merged_data


def write_csv(data, filename):
    with open(filename, 'w', newline='',encoding='utf-8') as csvfile:
        fieldnames = data[0].keys()
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        for row in data:
            writer.writerow(row)


if __name__ == "__main__":
    table1 = load_csv('D:\\qidian\\data\\9_region_code.csv')
    table2 = load_csv('D:\\qidian\\data\\8_region_code.csv')

    merged_data = merge_tables(table1, table2)

    write_csv(merged_data, 'D:\\qidian\\data\\merged_data.csv')
