package vn.techbox.techbox_store.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService { // Triển khai Interface

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override // Ghi đè phương thức từ interface
    public Map uploadFile(MultipartFile file, String folderName) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.asMap(
                "folder", folderName
            ));
    }

    @Override // Ghi đè phương thức từ interface
    public Map uploadVideo(MultipartFile file, String folderName) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.asMap(
                "resource_type", "video",
                "folder", folderName
            ));
    }

    @Override // Triển khai phương thức delete (Nếu bạn chọn thêm vào Interface)
    public Map deleteFile(String publicId) throws IOException {
        // Cloudinary cần biết loại tài nguyên (raw, image, video) để xóa.
        // Đối với tài nguyên chung (image), bạn có thể dùng mặc định là "image".
        // Tuy nhiên, để chính xác hơn, bạn có thể truyền thêm "resource_type" nếu cần phân biệt.
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}