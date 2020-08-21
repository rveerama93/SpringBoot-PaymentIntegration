package com.paypalexample.demo.controller;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.paypalexample.demo.model.Order;
import com.paypalexample.demo.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayPalController {
    @Autowired
   private PayPalService service;

    public static final String SUCCESS_URL="pay/success";
    public static final String CANCEL_URL="pay/cancel";

    @GetMapping("/")
public String home(){
    return "home";
}

@PostMapping("/pay")
public String payment(@ModelAttribute("order") Order order) throws PayPalRESTException {
      Payment payment= service.createPayment(order.getPrice(),order.getCurrency(),order.getMethod(),order.getIntent(),order.getDescription(),"http://localhost:9090/"+CANCEL_URL,"http://localhost:9090/"+SUCCESS_URL);
        for(Links link:payment.getLinks()){
            if(link.getRel().equals("approval_url")){
                return "redirect:"+link.getHref();
            }
        }
        return "redirect:/";
}

@GetMapping(value = CANCEL_URL)
    public String cancel(){
        return "cancel";
}

    @GetMapping(value = SUCCESS_URL)
    public  String successPay(@RequestParam("paymentId") String paymentId,@RequestParam("payerId") String payerId) throws PayPalRESTException {
        Payment payment=service.executePayment(paymentId,payerId);
        System.out.println(payment.toJSON());
        if(payment.getState().equals("Approved")){
            return "success";
        }
        return "redirecr:/";
    }

}
