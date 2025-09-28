package vn.techbox.techbox_store.voucher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherId implements Serializable {
    
    private Integer userId;
    private Integer voucherId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserVoucherId that = (UserVoucherId) o;
        
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return voucherId != null ? voucherId.equals(that.voucherId) : that.voucherId == null;
    }
    
    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (voucherId != null ? voucherId.hashCode() : 0);
        return result;
    }
}