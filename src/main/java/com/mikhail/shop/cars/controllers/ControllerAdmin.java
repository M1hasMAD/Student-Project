package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.exception.BusinessException;
import com.mikhail.shop.cars.exception.CategoryNotFoundException;
import com.mikhail.shop.cars.exception.DuplicateCategoryException;
import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.Category;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControllerAdmin {

    private final ServiceCars serviceCars;

    public ControllerAdmin(ServiceCars serviceCars) {
        this.serviceCars = serviceCars;
    }

    @GetMapping("/loginAdmin")
    public String loginAdmin() {
        return "admin/login_admin";
    }

    @PostMapping("/loginAdmin")
    public String loginAdminPost() {
        // The interceptor handles the password check
        return "redirect:/admin/startAdmin";
    }

    @GetMapping("/admin/startAdmin")
    public String startAdmin() {
        return "admin/start_admin";
    }

    @GetMapping("/logoutAdmin")
    public String logoutAdmin(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/loginAdmin";
    }

    // Category Management
    @GetMapping("/admin/manageCategories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", serviceCars.getCategories());
        return "admin/manage_categories";
    }

    @GetMapping("/admin/registerCategory")
    public String registerCategory(Model model) {
        model.addAttribute("newCategory", new Category());
        return "admin/manage_category";
    }

    @PostMapping("/admin/saveCategory")
    public String saveCategory(@ModelAttribute("newCategory") Category category,
                                RedirectAttributes redirectAttributes) {
        try {
            serviceCars.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully!");
            return "redirect:/admin/manageCategories";
        } catch (DuplicateCategoryException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/registerCategory";
        }
    }

    @GetMapping("/admin/editCategory")
    public String editCategory(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = serviceCars.getCategoryById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));
            model.addAttribute("categoryToEdit", category);
            return "admin/edit_category";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/manageCategories";
        }
    }

    @PostMapping("/admin/saveChangesOfCategory")
    public String saveChangesOfCategory(@ModelAttribute("categoryToEdit") Category category,
                                         RedirectAttributes redirectAttributes) {
        try {
            serviceCars.updateCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
            return "redirect:/admin/manageCategories";
        } catch (DuplicateCategoryException | CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/editCategory?id=" + category.getId();
        }
    }

    @GetMapping("/admin/deleteCategory")
    public String deleteCategory(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceCars.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully!");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manageCategories";
    }
}