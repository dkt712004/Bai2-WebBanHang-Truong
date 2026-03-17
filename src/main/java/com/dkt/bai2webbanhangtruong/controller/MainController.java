package com.dkt.bai2webbanhangtruong.controller;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.dkt.bai2webbanhangtruong.dao.OrderDAO;
import com.dkt.bai2webbanhangtruong.dao.ProductDAO;
import com.dkt.bai2webbanhangtruong.entity.Product;
import com.dkt.bai2webbanhangtruong.model.CartInfo;
import com.dkt.bai2webbanhangtruong.model.ProductInfo;
import com.dkt.bai2webbanhangtruong.pagination.PaginationResult;
import com.dkt.bai2webbanhangtruong.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Transactional
public class MainController {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    // Sử dụng Constructor Injection để xóa cảnh báo "Field injection is not recommended"
    public MainController(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    @RequestMapping("/403")
    public String accessDenied() { return "403"; }

    @RequestMapping("/")
    public String home() { return "index"; }

    @RequestMapping({ "/productList" })
    public String listProductHandler(Model model,
                                     @RequestParam(value = "name", defaultValue = "") String likeName,
                                     @RequestParam(value = "page", defaultValue = "1") int page) {
        final int maxResult = 5;
        final int maxNavigationPage = 10;
        PaginationResult<ProductInfo> result = productDAO.queryProducts(page, maxResult, maxNavigationPage, likeName);
        model.addAttribute("paginationProducts", result);
        return "productList";
    }

    @RequestMapping({ "/buyProduct" })
    public String buyProductHandler(HttpServletRequest request,
                                    @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && !code.isEmpty()) {
            product = productDAO.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = Utils.getCartInSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.addProduct(productInfo, 1);
        }
        return "redirect:/shoppingCart";
    }

    @RequestMapping({ "/shoppingCartRemoveProduct" })
    public String removeProductHandler(HttpServletRequest request,
                                       @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && !code.isEmpty()) {
            product = productDAO.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = Utils.getCartInSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.removeProduct(productInfo);
        }
        return "redirect:/shoppingCart";
    }

    @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
    public String shoppingCartHandler(HttpServletRequest request, Model model) {
        CartInfo myCart = Utils.getCartInSession(request);
        model.addAttribute("cartForm", myCart);
        return "shoppingCart";
    }

    @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
    public String shoppingCartUpdateQty(HttpServletRequest request, @ModelAttribute("cartForm") CartInfo cartForm) {
        CartInfo cartInfo = Utils.getCartInSession(request);
        cartInfo.updateQuantity(cartForm);
        return "redirect:/shoppingCart";
    }

    // Đã lược bỏ bước nhập thông tin khách hàng (shoppingCartCustomer) theo yêu cầu

    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
    public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
        CartInfo cartInfo = Utils.getCartInSession(request);
        // Đã xóa check 'cartInfo == null' dư thừa
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        }
        model.addAttribute("myCart", cartInfo);
        return "shoppingCartConfirmation";
    }

    @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
    public String shoppingCartConfirmationSave(HttpServletRequest request) {
        CartInfo cartInfo = Utils.getCartInSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        }
        try {
            orderDAO.saveOrder(cartInfo);
        } catch (Exception e) {
            return "shoppingCartConfirmation";
        }
        Utils.removeCartInSession(request);
        Utils.storeLastOrderedCartInSession(request, cartInfo);
        return "redirect:/shoppingCartFinalize";
    }

    @RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
    public String shoppingCartFinalize(HttpServletRequest request, Model model) {
        CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
        if (lastOrderedCart == null) {
            return "redirect:/shoppingCart";
        }
        model.addAttribute("lastOrderedCart", lastOrderedCart);
        return "shoppingCartFinalize";
    }

    @RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
    public void productImage(HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        Product product = null;
        if (code != null) product = this.productDAO.findProduct(code);
        if (product != null && product.getImage() != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(product.getImage());
        }
        response.getOutputStream().close();
    }
}