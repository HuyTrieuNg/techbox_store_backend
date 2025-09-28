package vn.techbox.techbox_store.promotion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Thông tin quản lý sự kiện
    @Column(nullable = false, unique = true, length = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 255)
    private String image;
    
    @Column(name = "image_id", length = 255)
    private String imageID;
    
    // Khung thời gian và trạng thái chung
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    // Quản lý thời gian
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Quan hệ với promotions
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Promotion> promotions;
    
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Utility methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }
    
    public boolean isScheduled() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(startDate);
    }
    
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(endDate);
    }
    
    public String getStatus() {
        if (isScheduled()) return "SCHEDULED";
        if (isActive()) return "ACTIVE";
        if (isExpired()) return "EXPIRED";
        return "INACTIVE";
    }
}