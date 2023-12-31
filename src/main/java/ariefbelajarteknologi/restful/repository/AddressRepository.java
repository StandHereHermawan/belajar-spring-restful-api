package ariefbelajarteknologi.restful.repository;

import ariefbelajarteknologi.restful.entity.Address;
import ariefbelajarteknologi.restful.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    Optional<Address> findFirstByContactAndId(Contact contact, String id);
}
