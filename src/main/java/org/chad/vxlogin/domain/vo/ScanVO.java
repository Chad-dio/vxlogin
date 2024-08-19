package org.chad.vxlogin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanVO {
    private Boolean scan;
    private String scanMsg;
}
