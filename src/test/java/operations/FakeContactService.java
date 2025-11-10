package operations;

import org.example.repository.ContactRepository;
import org.example.service.ContactService;

public class FakeContactService extends ContactService {
    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     *
     * @param repository
     */
    public FakeContactService(ContactRepository repository) {
        super(repository);
    }
}
