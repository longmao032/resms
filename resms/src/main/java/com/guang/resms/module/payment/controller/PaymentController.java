package com.guang.resms.module.payment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.module.payment.entity.dto.PaymentDTO;
import com.guang.resms.module.payment.entity.dto.PaymentQueryDTO;
import com.guang.resms.module.payment.entity.vo.PaymentStatisticsVO;
import com.guang.resms.module.payment.entity.vo.PaymentVO;
import com.guang.resms.module.payment.service.PaymentService;
import com.guang.resms.common.exception.ResponseResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 分页查询列表
     */
    @GetMapping("/list")
    public ResponseResult<IPage<PaymentVO>> list(PaymentQueryDTO queryDTO) {
        return ResponseResult.success(paymentService.getPaymentPage(queryDTO));
    }

    /**
     * 新增收款
     */
    @PostMapping
    public ResponseResult<Void> add(@Valid @RequestBody PaymentDTO paymentDTO) {
        paymentService.addPayment(paymentDTO);
        return ResponseResult.success("新增成功");
    }

    /**
     * 确认收款 (业务流转)
     */
    @PutMapping("/{id}/confirm")
    public ResponseResult<Void> confirm(@PathVariable Integer id) {
        paymentService.confirmPayment(id);
        return ResponseResult.success("确认成功");
    }

    /**
     * 作废收款
     */
    @PutMapping("/{id}/void")
    public ResponseResult<Void> voidPayment(@PathVariable Integer id, @RequestParam String reason) {
        paymentService.voidPayment(id, reason);
        return ResponseResult.success("作废成功");
    }

    /**
     * 修改收款记录
     */
    @PutMapping
    public ResponseResult<Void> update(@Valid @RequestBody PaymentDTO paymentDTO) {
        paymentService.updatePayment(paymentDTO);
        return ResponseResult.success("修改成功");
    }

    @GetMapping("/statistics")
    public ResponseResult<PaymentStatisticsVO> getStatistics() {
        return ResponseResult.success(paymentService.getStatistics());
    }

    /**
     * 上传收款凭证图片
     * 
     * @param file          图片文件
     * @param transactionId 交易ID（用于命名）
     * @return 图片相对路径
     */
    @PostMapping("/uploadProof")
    public ResponseResult<String> uploadProof(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("transactionId") Integer transactionId) {
        try {
            // 验证文件
            if (!com.guang.resms.common.utils.FileUploadUtils.isValidImage(file)) {
                return ResponseResult.fail("文件无效，请上传5MB以内的图片文件（jpg/png/gif/webp）");
            }

            // 生成文件名：交易ID + 时间戳 + 随机数
            String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    .format(java.time.LocalDateTime.now());
            String random = String.format("%04d", new java.util.Random().nextInt(10000));
            String fileName = "TX" + transactionId + "_" + timestamp + "_" + random;

            // 保存到 proof 目录
            String relativePath = com.guang.resms.common.utils.FileUploadUtils.saveFile(file, "proof", fileName);

            return ResponseResult.success("上传成功", relativePath);
        } catch (Exception e) {
            return ResponseResult.fail("上传失败: " + e.getMessage());
        }
    }
}