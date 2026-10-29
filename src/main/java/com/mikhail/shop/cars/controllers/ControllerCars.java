package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.interfaces.ServiceCars;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class ControllerCars {

    private static final Logger log = LoggerFactory.getLogger(ControllerCars.class);

    private final ServiceCars serviceCars;

    public ControllerCars(ServiceCars serviceCars) {
        this.serviceCars = serviceCars;
    }

    @GetMapping("/manageCars")
    public String manageCars(Model model) {
        model.addAttribute("cars", serviceCars.getCars());
        return "admin/manage_products";
    }

    @GetMapping("/registerCar")
    public String registerCar(Model model) {
        Car newCar = new Car();
        newCar.setPrice(1);

        model.addAttribute("newCar", newCar);
        model.addAttribute("categories", serviceCars.getCategories());

        return "admin/manage_product";
    }

    @PostMapping("/saveCar")
    public String saveCar(
            @ModelAttribute("newCar")
            @Valid
            Car newCar,
            BindingResult validationResult,
            Model model) {

        log.info("Received car to register: {}", newCar);

        if (validationResult.hasErrors()) {
            model.addAttribute("categories", serviceCars.getCategories());
            return "admin/manage_product";
        }

        serviceCars.registerCar(newCar);

        return "admin/register_product_ok";
    }


    @PostMapping("/saveChangesOfCar")
    public String saveChangesOfCar(
            @ModelAttribute("carToEdit")
            @Valid
            Car carToEdit,
            BindingResult validationResult,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {

        if (validationResult.hasErrors()) {
            model.addAttribute("categories", serviceCars.getCategories());
            return "admin/edit_product";
        }

        serviceCars.saveChangesOfCar(carToEdit, categoryId);

        return "redirect:/admin/manageCars";
    }


    @GetMapping("/editCar")
    public String editCar(@RequestParam("id") Long id, Model model) {

        var carOptional = serviceCars.getCarById(id);
        if (carOptional.isEmpty()) {
            return "redirect:/admin/manageCars";
        }

        model.addAttribute(
                "carToEdit",
                carOptional.get()
        );

        model.addAttribute(
                "categories",
                serviceCars.getCategories()
        );

        return "admin/edit_product";
    }

    @PostMapping("/deleteCar")
    public String deleteCar(@RequestParam("id") Long id,
                            Model model) {

        log.info("Received id to delete: {}", id);

        serviceCars.deleteCarById(id);

        return manageCars(model);
    }
}