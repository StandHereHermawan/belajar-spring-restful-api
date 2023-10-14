package ariefbelajarteknologi.restful.controller;

import ariefbelajarteknologi.restful.entity.User;
import ariefbelajarteknologi.restful.model.AddressResponse;
import ariefbelajarteknologi.restful.model.CreateAddressRequest;
import ariefbelajarteknologi.restful.model.WebResponse;
import ariefbelajarteknologi.restful.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{contactId}/addresses"
    )
    public WebResponse<AddressResponse> create(
            User user,
            @RequestBody CreateAddressRequest request,
            @PathVariable("contactId") String contactId)
    {
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }
}
