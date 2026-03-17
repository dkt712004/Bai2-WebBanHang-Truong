package com.dkt.bai2webbanhangtruong.controller;

import java.util.List;
import com.dkt.bai2webbanhangtruong.dao.OrderDAO;
import com.dkt.bai2webbanhangtruong.dao.ProductDAO;
import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.form.ProductForm;
import com.dkt.bai2webbanhangtruong.model.OrderDetailInfo;
import com.dkt.bai2webbanhangtruong.model.OrderInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.validator.ProductFormValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@Transactional
public class AdminController {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final ProductFormValidator productFormValidator;

    // Constructor Injection: Cách làm chuẩn để Spring tiêm các Bean vào
    public AdminController(OrderDAO orderDAO,
                           ProductDAO productDAO,
                           ProductFormValidator productFormValidator) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.productFormValidator = productFormValidator;
    }

    // Kết nối bộ kiểm tra dữ liệu cho Form sản phẩm
    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) return;
        if (target.getClass() == ProductForm.class) {
            dataBinder.setValidator(productFormValidator);
        }
    }

    // [GET] Hiển thị trang Đăng nhập
    @RequestMapping(value = { "/admin/login" }, method = RequestMethod.GET)
    public String login(Model model) {
        return "login";
    }

    // [GET] Hiển thị thông tin tài khoản sau khi đăng nhập thành công
    @RequestMapping(value = { "/admin/accountInfo" }, method = RequestMethod.GET)
    public String accountInfo(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userDetails", userDetails);
        return "accountInfo";
    }

    // [GET] Hiển thị danh sách đơn hàng (có phân trang)
    @RequestMapping(value = { "/admin/orderList" }, method = RequestMethod.GET)
    public String orderList(Model model,
                            @RequestParam(value = "page", defaultValue = "1") int page) {
        final int MAX_RESULT = 5;
        final int MAX_NAVIGATION_PAGE = 10;

        PaginationResult<OrderInfo> paginationResult = orderDAO.listOrderInfo(page, MAX_RESULT, MAX_NAVIGATION_PAGE);
        model.addAttribute("paginationResult", paginationResult);
        return "orderList";
    }

    // [GET] Hiển thị chi tiết một đơn hàng
    @RequestMapping(value = { "/admin/order" }, method = RequestMethod.GET)
    public String orderView(Model model, @RequestParam("orderId") Long orderId) {
        OrderInfo orderInfo = orderDAO.getOrderInfo(orderId);
        if (orderInfo == null) {
            return "redirect:/admin/orderList";
        }
        List<OrderDetailInfo> details = orderDAO.listOrderDetailInfos(orderId);
        orderInfo.setDetails(details);
        model.addAttribute("orderInfo", orderInfo);
        return "order";
    }

    // [GET] Hiển thị form Thêm mới hoặc Sửa sản phẩm
    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.GET)
    public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
        ProductForm productForm = null;

        if (!code.isEmpty()) {
            Product product = productDAO.findProduct(code);
            if (product != null) {
                productForm = new ProductForm(product);
            }
        }
        if (productForm == null) {
            productForm = new ProductForm();
            productForm.setNewProduct(true);
        }
        model.addAttribute("productForm", productForm);
        return "product";
    }

    // [POST] Xử lý lưu sản phẩm vào Database
    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.POST)
    public String productSave(Model model,
                              @ModelAttribute("productForm") @Validated ProductForm productForm,
                              BindingResult result) {

        if (result.hasErrors()) {
            return "product";
        }
        try {
            productDAO.save(productForm);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "product";
        }
        return "redirect:/productList";
    }
}