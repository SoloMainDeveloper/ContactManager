package operations;

import org.example.entity.Contact;
import org.example.repository.ContactRepository;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.*;

/**
 * Хранилище контактов. Необходимо для тестов
 */
public class FakeContactRepository extends ContactRepository {
    private final Map<Long, Map<String, Contact>> contacts;

    /**
     * Конструктор
     */
    public FakeContactRepository() {
        super(new PGSimpleDataSource());
        contacts = new LinkedHashMap<>();
    }

    /**
     * Возвращает количество контактов у пользователя с данным chatId
     */
    public int getCurrentContactsSize(Long chatId){
        if(contacts.containsKey(chatId)){
            return contacts.get(chatId).size();
        }
        return 0;
    }

    @Override
    public void add(Contact contact) {
        Long chatId = contact.getChatId();
        if(!contacts.containsKey(chatId)){
            contacts.put(chatId, new LinkedHashMap<>());
        }
        if(contacts.get(chatId).containsKey(contact.getName())){
            throw new RuntimeException();
        }
        contacts.get(chatId).put(contact.getName(), contact);
    }

    @Override
    public Optional<Contact> findContactByName(String name, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        return currentChatIdContacts == null
            ? Optional.empty()
            : Optional.of(currentChatIdContacts.get(name));
    }

    @Override
    public Optional<Contact> findContactByNumber(String number, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        for (Contact current : currentChatIdContacts.values()) {
            if (Objects.equals(current.getPhoneNumber(), number)) {
                return Optional.of(current);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Contact> findContactsByChatId(Long chatId, String filter, String sorter) {
//        String sql = "SELECT * FROM public.contacts WHERE chat_id = :chatId" + filter + sorter;
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("chatId", chatId);
//
//        try {
//            return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
//                ContactMapper mapper = new ContactMapper();
//                return mapper.resultSetToContactEntity(resultSet);
//            });
//        } catch (EmptyResultDataAccessException exception) {
//            return null;
//        }
        return null;
    }

    @Override
    public void updateBlockField(Contact contact) {
        update(contact);
    }

    @Override
    public void update(Contact contact) {
        Map<String, Contact> currentChatIdContacts = contacts.get(contact.getChatId());
        if(currentChatIdContacts != null){
            currentChatIdContacts.put(contact.getName(), contact);
        }
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        if(currentChatIdContacts == null) {
            return;
        }
        currentChatIdContacts.remove(name);
    }
}
