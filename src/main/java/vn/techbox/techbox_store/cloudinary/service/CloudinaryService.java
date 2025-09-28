package vn.techbox.techbox_store.cloudinary.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    /**
     * Tải tệp hình ảnh lên Cloudinary.
     * @param file Tệp hình ảnh dưới dạng MultipartFile.
     * @param folderName Tên thư mục lưu trữ trên Cloudinary (ví dụ: "product_images").
     * @return Map chứa phản hồi từ Cloudinary, bao gồm "public_id" và "secure_url".
     * @throws IOException Nếu quá trình đọc tệp hoặc kết nối gặp lỗi.
     */
    Map uploadFile(MultipartFile file, String folderName) throws IOException;

    /**
     * Tải tệp video lên Cloudinary.
     * @param file Tệp video dưới dạng MultipartFile.
     * @param folderName Tên thư mục lưu trữ trên Cloudinary.
     * @return Map chứa phản hồi từ Cloudinary.
     * @throws IOException Nếu quá trình đọc tệp hoặc kết nối gặp lỗi.
     */
    Map uploadVideo(MultipartFile file, String folderName) throws IOException;
    
    /**
     * Tùy chọn: Phương thức xóa tài nguyên khỏi Cloudinary bằng public ID.
     * Bạn có thể cân nhắc thêm phương thức này cho việc quản lý tài nguyên.
     * @param publicId Mã định danh công khai của tài nguyên cần xóa.
     * @return Map chứa phản hồi từ Cloudinary.
     * @throws IOException Nếu kết nối gặp lỗi.
     */
    Map deleteFile(String publicId) throws IOException;
}