package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceCategories;
import com.mikhail.shop.cars.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class ControllerCategories {

    private static final Logger log = LoggerFactory.getLogger(ControllerCategories.class);

    private final ServiceCategories serviceCategories;

    public ControllerCategories(ServiceCategories serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    @GetMapping("/manageCategories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", serviceCategories.getCategories());
        return "admin/manage_categories";
    }

    @GetMapping("/registerCategory")
    public String registerCategory(Model model) {
        model.addAttribute("newCategory", new Category());
        return "admin/manage_category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(Category newCategory) {
        serviceCategories.registerCategory(newCategory);
        return "redirect:/admin/manageCategories";
    }

    @GetMapping("/editCategory")
    public String editCategory(@RequestParam("id") long id, Model model) {
        var categoryOptional = serviceCategories.getCategoryById(id);

        if (categoryOptional.isEmpty()) {
            return "redirect:/admin/manageCategories";
        }

        model.addAttribute("categoryToEdit", categoryOptional.get());
        return "admin/edit_category";
    }

    @PostMapping("/saveChangesOfCategory")
    public String saveChangesOfCategory(Category categoryToEdit) {
        serviceCategories.saveChangesOfCategory(categoryToEdit);
        return "redirect:/admin/manageCategories";
    }

    @PostMapping("/deleteCategory")
    public String deleteCategory(@RequestParam("id") long id, Model model) {
        try {
            serviceCategories.deleteCategoryById(id);
        } catch (Exception e) {
            log.error("Error deleting category with id: {}", id, e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", serviceCategories.getCategories());
            return "admin/manage_categories";
        }

        return "redirect:/admin/manageCategories";
    }
}