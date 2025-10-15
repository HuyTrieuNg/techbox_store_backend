package vn.techbox.techbox_store.voucher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherId implements Serializable {
    
    private Integer userId;
    private String voucherCode;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserVoucherId that = (UserVoucherId) o;
        
    if (!Objects.equals(userId, that.userId)) return false;
    return Objects.equals(voucherCode, that.voucherCode);
    }
    
    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
    result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        return result;
    }
}