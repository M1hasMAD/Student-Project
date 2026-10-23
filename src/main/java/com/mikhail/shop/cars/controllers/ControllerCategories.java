package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceCategories;
import com.mikhail.shop.cars.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerCategories {

    @Autowired
    private ServiceCategories serviceCategories;

    @RequestMapping("admin/manageCategories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", serviceCategories.getCategories());
        return "admin/manage_categories";
    }

    @RequestMapping("admin/registerCategory")
    public String registerCategory(Model model) {
        model.addAttribute("newCategory", new Category());
        return "admin/manage_category";
    }

    @RequestMapping("admin/saveCategory")
    public String saveCategory(Category newCategory) {
        serviceCategories.registerCategory(newCategory);
        return "redirect:/admin/manageCategories";
    }

    @RequestMapping("admin/editCategory")
    public String editCategory(@RequestParam("id") long id, Model model) {
        Category category = serviceCategories.getCategoryById(id);

        if (category == null) {
            return "redirect:/admin/manageCategories";
        }

        model.addAttribute("categoryToEdit", category);
        return "admin/edit_category";
    }

    @RequestMapping("admin/saveChangesOfCategory")
    public String saveChangesOfCategory(Category categoryToEdit) {
        serviceCategories.saveChangesOfCategory(categoryToEdit);
        return "redirect:/admin/manageCategories";
    }

    @RequestMapping("admin/deleteCategory")
    public String deleteCategory(@RequestParam("id") long id, Model model) {
        try {
            serviceCategories.deleteCategoryById(id);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", serviceCategories.getCategories());
            return "admin/manage_categories";
        }

        return "redirect:/admin/manageCategories";
    }
}