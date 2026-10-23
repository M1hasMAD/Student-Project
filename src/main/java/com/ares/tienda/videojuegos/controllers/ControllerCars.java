package com.ares.tienda.videojuegos.controllers;

import com.ares.tienda.videojuegos.model.Car;
import com.ares.tienda.videojuegos.interfaces.ServiceCars;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin/")
public class ControllerCars {

    @Autowired
    private ServiceCars serviceCars;

    @RequestMapping("manageCars")
    public String manageCars(Model model) {
        model.addAttribute("cars", serviceCars.getCars());
        return "admin/manage_products";
    }

    @RequestMapping("registerCar")
    public String registerCar(Model model) {
        Car newCar = new Car();
        newCar.setPrice(1);

        model.addAttribute("newCar", newCar);
        model.addAttribute("categories", serviceCars.getCategories());

        return "admin/manage_product";
    }

    @RequestMapping("saveCar")
    public String saveCar(
            @ModelAttribute("newCar")
            @Valid
            Car newCar,
            BindingResult validationResult,
            Model model) {

        System.out.println("Received car to register: " + newCar);

        if (validationResult.hasErrors()) {
            model.addAttribute("categories", serviceCars.getCategories());
            return "admin/manage_product";
        }

        serviceCars.registerCar(newCar);

        return "admin/register_product_ok";
    }


    @RequestMapping("saveChangesOfCar")
    public String saveChangesOfCar(
            @ModelAttribute("carToEdit")
            @Valid
            Car carToEdit,
            BindingResult validationResult,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) throws Exception {

        if (validationResult.hasErrors()) {
            model.addAttribute("categories", serviceCars.getCategories());
            return "admin/edit_product";
        }

        serviceCars.saveChangesOfCar(carToEdit, categoryId);

        return "redirect:/admin/manageCars";
    }


    @RequestMapping("editCar")
    public String editCar(@RequestParam("id") Long id, Model model) {

        model.addAttribute(
                "carToEdit",
                serviceCars.getCarById(id)
        );

        model.addAttribute(
                "categories",
                serviceCars.getCategories()
        );

        return "admin/edit_product";
    }

    @RequestMapping("deleteCar")
    public String deleteCar(@RequestParam("id") Long id,
                            Model model) throws Exception {

        System.out.println("Received id to delete: " + id);

        serviceCars.deleteCarById(id);

        return manageCars(model);
    }
}