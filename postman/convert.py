import csv
import json
import re

# Định nghĩa các trường cần chuyển đổi kiểu dữ liệu (tương tự như trước)
NUMERIC_FIELDS = {
    "value": float,
    "minOrderAmount": float,
    "usageLimit": int
}

# Định nghĩa các trường ngày giờ
DATE_FIELDS = ["validFrom", "validUntil"]

# Regex đơn giản để kiểm tra định dạng ngày giờ ISO 8601 (YYYY-MM-DDTHH:MM:SS)
DATE_REGEX = re.compile(r'\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$')

def clean_and_convert_value(key, value):
    """
    Xử lý giá trị cho chiều CSV -> JSON: 
    - Loại bỏ khoảng trắng/xử lý rỗng.
    - Chuyển đổi số.
    - Thêm 'Z' cho ngày giờ.
    """
    if value is None:
        return None
    
    clean_value = str(value).strip()
    
    # Nếu giá trị là chuỗi rỗng sau khi trim, trả về None
    if clean_value == '':
        return None

    # --- 1. XỬ LÝ TRƯỜNG NGÀY GIỜ ---
    if key in DATE_FIELDS:
        if DATE_REGEX.match(clean_value) and not clean_value.endswith('Z'):
            return clean_value + 'Z'
        return clean_value

    # --- 2. XỬ LÝ TRƯỜNG SỐ ---
    if key in NUMERIC_FIELDS:
        clean_value = clean_value.replace(',', '')
        target_type = NUMERIC_FIELDS[key]
        
        try:
            return target_type(clean_value)
        except ValueError:
            return clean_value
    
    # --- 3. XỬ LÝ CÁC TRƯỜNG CHUỖI KHÁC ---
    return clean_value


def convert_csv_to_json(csv_filepath, json_filepath):
    """ Đọc file CSV và ghi ra file JSON. """
    data = []
    
    try:
        with open(csv_filepath, mode='r', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                processed_row = {}
                for key, value in row.items():
                    clean_key = key.strip()
                    processed_row[clean_key] = clean_and_convert_value(clean_key, value)
                data.append(processed_row)
            
    except FileNotFoundError:
        print(f"Lỗi: Không tìm thấy file CSV tại đường dẫn {csv_filepath}")
        return
    except Exception as e:
        print(f"Lỗi trong quá trình đọc file CSV: {e}")
        return

    # Ghi dữ liệu ra file JSON
    try:
        with open(json_filepath, mode='w', encoding='utf-8') as jsonfile:
            json.dump(data, jsonfile, ensure_ascii=False, indent=4)
        print(f"Thành công! Đã chuyển đổi {len(data)} dòng dữ liệu sang {json_filepath}")
    except Exception as e:
        print(f"Lỗi trong quá trình ghi file JSON: {e}")


# ----------------------------------------------------------------------------------

def convert_json_to_csv(json_filepath, csv_filepath):
    """ Đọc file JSON và ghi ra file CSV. """
    try:
        with open(json_filepath, mode='r', encoding='utf-8') as jsonfile:
            data = json.load(jsonfile)
    except FileNotFoundError:
        print(f"Lỗi: Không tìm thấy file JSON tại đường dẫn {json_filepath}")
        return
    except json.JSONDecodeError:
        print(f"Lỗi: File {json_filepath} không phải là JSON hợp lệ.")
        return

    if not data:
        print("File JSON trống, không có gì để ghi ra CSV.")
        return

    # Lấy header (tên cột) từ keys của đối tượng đầu tiên
    fieldnames = list(data[0].keys())

    # Ghi dữ liệu ra file CSV
    try:
        with open(csv_filepath, mode='w', encoding='utf-8', newline='') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader() # Ghi tiêu đề cột
            
            for row in data:
                # Xử lý các giá trị None/null trong JSON thành chuỗi rỗng trong CSV
                processed_row = {k: '' if v is None else v for k, v in row.items()}
                writer.writerow(processed_row)

        print(f"Thành công! Đã chuyển đổi {len(data)} đối tượng sang {csv_filepath}")
    except Exception as e:
        print(f"Lỗi trong quá trình ghi file CSV: {e}")


# ----------------------------------------------------------------------------------

if __name__ == "__main__":
    
    print("\n--- CÔNG CỤ CHUYỂN ĐỔI ĐỊNH DẠNG DỮ LIỆU (CSV/JSON) ---")
    
    # Hỏi chế độ chuyển đổi
    while True:
        mode = input("Chọn chế độ chuyển đổi (1: CSV -> JSON, 2: JSON -> CSV): ").strip()
        if mode in ['1', '2']:
            break
        print("Vui lòng chỉ nhập 1 hoặc 2.")

    if mode == '1':
        # Chế độ CSV -> JSON
        input_default = 'voucher_test_cases.csv'
        output_default = 'new_voucher_test_cases.json'
        
        input_file = input(f"Nhập tên file đầu vào CSV (Mặc định: {input_default}): ").strip() or input_default
        output_file = input(f"Nhập tên file đầu ra JSON (Mặc định: {output_default}): ").strip() or output_default
        
        print(f"\n🔄 Đang chuyển đổi {input_file} -> {output_file}...")
        convert_csv_to_json(input_file, output_file)

    elif mode == '2':
        # Chế độ JSON -> CSV
        input_default = 'voucher_test_cases.json'
        output_default = 'new_voucher_test_cases.csv'
        
        input_file = input(f"Nhập tên file đầu vào JSON (Mặc định: {input_default}): ").strip() or input_default
        output_file = input(f"Nhập tên file đầu ra CSV (Mặc định: {output_default}): ").strip() or output_default
        
        print(f"\n🔄 Đang chuyển đổi {input_file} -> {output_file}...")
        convert_json_to_csv(input_file, output_file)

    print("\nQuá trình hoàn tất.")