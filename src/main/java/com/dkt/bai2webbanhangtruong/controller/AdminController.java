package com.dkt.bai2webbanhangtruong.controller;

import com.dkt.bai2webbanhangtruong.dao.OrderDAO;
import com.dkt.bai2webbanhangtruong.dao.ProductDAO;
import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.form.ProductForm;
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

    public AdminController(OrderDAO orderDAO, ProductDAO productDAO, ProductFormValidator productFormValidator) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.productFormValidator = productFormValidator;
    }

    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) return;
        if (target.getClass() == ProductForm.class) {
            dataBinder.setValidator(productFormValidator);
        }
    }

    @RequestMapping(value = { "/admin/login" }, method = RequestMethod.GET)
    public String login() {
        return "login";
    }


    @RequestMapping(value = { "/admin/accountInfo" }, method = RequestMethod.GET)
    public String accountInfo(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userDetails", userDetails);
        return "accountInfo";
    }

    @RequestMapping(value = { "/admin/orderList" }, method = RequestMethod.GET)
    public String orderList(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        PaginationResult<OrderInfo> paginationResult = orderDAO.listOrderInfo(page, 5, 10);
        model.addAttribute("paginationResult", paginationResult);
        return "orderList";
    }

    @RequestMapping(value = { "/admin/order" }, method = RequestMethod.GET)
    public String orderView(Model model, @RequestParam("orderId") Long orderId) {
        OrderInfo orderInfo = orderDAO.getOrderInfo(orderId);
        if (orderInfo == null) return "redirect:/admin/orderList";
        orderInfo.setDetails(orderDAO.listOrderDetailInfos(orderId));
        model.addAttribute("orderInfo", orderInfo);
        return "order";
    }

    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.GET)
    public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
        ProductForm productForm = null;
        if (code != null && !code.isEmpty()) {
            Product product = productDAO.findProduct(code);
            if (product != null) productForm = new ProductForm(product);
        }
        if (productForm == null) productForm = new ProductForm();
        model.addAttribute("productForm", productForm);
        return "product";
    }

    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.POST)
    public String productSave(Model model, @ModelAttribute("productForm") @Validated ProductForm productForm, BindingResult result) {
        if (result.hasErrors()) return "product";
        try {
            productDAO.save(productForm);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "product";
        }
        return "redirect:/productList";
    }

    @RequestMapping(value = { "/admin/order/updateStatus" }, method = RequestMethod.POST)
    public String updateStatus(@RequestParam("orderId") Long orderId, @RequestParam("status") String status) {
        orderDAO.updateStatus(orderId, status);
        return "redirect:/admin/order?orderId=" + orderId;
    }



    @RequestMapping(value = { "/admin/productEdit" }, method = RequestMethod.GET)
    public String editProductHandler(Model model, @RequestParam("code") String code) {
        System.out.println("Dang sua san pham co ma: " + code);

        Product product = productDAO.findProduct(code);

        if (product == null) {
            System.out.println("LOI: Khong tim thay san pham trong DB!");
            return "redirect:/productList";
        }

        System.out.println("Da tim thay: " + product.getName());

        ProductForm productForm = new ProductForm(product);
        model.addAttribute("productForm", productForm);
        return "productEdit";
    }


    @RequestMapping(value = { "/admin/productEdit" }, method = RequestMethod.POST)
    public String saveEditProduct(Model model,
                                  @ModelAttribute("productForm") @Validated ProductForm productForm,
                                  BindingResult result) {

        if (result.hasErrors()) {
            return "productEdit";
        }

        try {

            productDAO.save(productForm);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
            return "productEdit";
        }

        return "redirect:/productList";
    }
}