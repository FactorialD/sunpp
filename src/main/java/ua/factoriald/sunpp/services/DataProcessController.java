package ua.factoriald.sunpp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Клас зберігає методи, що працюють з даними з бази даних і опрацьовують їх для зручного користування
 *
 * В цьому класі є методи, що кидають {@link DataProcessException}
 * Це зроблено для зручного використання цих методів.
 * Для того щоб правильно використати такі методи, їх потрібно викликати в блоці @try-@catch
 * При помилці доступу чи неправильних даних блок @catch зловить виключення і виконає потрібні дії
 *
 * Див. {@link DataProcessException}
 */
@Component
public class DataProcessController {

    private final UserRepository userRepository;
    private final UserHaveAccessToServiceRepository accessRepository;
    private final RoleRepository roleRepository;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;
    private final WorkerRepository workerRepository;
    private final DepartmentRepository departmentRepository;
    private final CheckTypeRepository checkTypeRepository;

    @Autowired
    public DataProcessController(UserRepository userRepository, UserHaveAccessToServiceRepository accessRepository, RoleRepository roleRepository, ServiceRepository serviceRepository, ApplicationRepository applicationRepository, WorkerRepository workerRepository, DepartmentRepository departmentRepository, CheckTypeRepository checkTypeRepository) {
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.roleRepository = roleRepository;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
        this.workerRepository = workerRepository;
        this.departmentRepository = departmentRepository;
        this.checkTypeRepository = checkTypeRepository;
    }

    /**
     * Повертає користувача за ідентифікатором, якщо він має потрібну роль
     * @param userId Ідентифікатор користувача
     * @param role Потрібна роль користувача
     * @return Користувач
     * @throws DataProcessException, якщо немає такого користувача чи у нього немає такої ролі
     */
    public UserEntity getUserWithRoleOrThrow(Long userId, RoleEntity role) throws DataProcessException {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            throw new DataProcessException("Немає такого користувача");
        }else{
            UserEntity user = userOpt.get();
            if(accessRepository.getAllByUserAndRole(user, role).size() == 0){
                throw new DataProcessException("Цей користувач не має потрібних прав");
            }else{
                //Все в порядку, цей користувач існує і в нього є потрібні права
                return user;
            }
        }
    }

    /**
     * Повертає користувача за ідентифікатором
     * @param userId Ідентифікатор користувача
     * @return Користувач
     * @throws DataProcessException, якщо немає такого користувача
     */
    public UserEntity getUserOrThrow(Long userId) throws DataProcessException {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            throw new DataProcessException("Немає такого користувача");
        }else{
            return userOpt.get();
        }
    }

    /**
     * Повертає робітника за ідентифікатором
     * @param workerId Ідентифікатор робітника
     * @return Робітник
     * @throws DataProcessException, якщо немає такого робітника
     */
    public WorkerEntity getWorkerOrThrow(Long workerId) throws DataProcessException {
        Optional<WorkerEntity> workerOpt = workerRepository.findById(workerId);
        if(!workerOpt.isPresent()){
            throw new DataProcessException("Немає такого працівника");
        }else{
            return workerOpt.get();
        }
    }

    /**
     * Повертає сервіс за ідентифікатором
     * @param serviceId Ідентифікатор сервісу
     * @return Сервіс
     * @throws DataProcessException, якшо немає такого сервісу
     */
    public ServiceEntity getServiceOrThrow(Long serviceId) throws DataProcessException {
        Optional<ServiceEntity> serviceOpt = serviceRepository.findById(serviceId);
        if(!serviceOpt.isPresent()){
            throw new DataProcessException("Немає такого сервісу");
        }
        return serviceOpt.get();
    }

    /**
     * Повертає підрозділ за ідентифікатором
     * @param departmentId Ідентифікатор підрозділу
     * @return Підрозділ
     * @throws DataProcessException, якщо немає такого підрозділу
     */
    public DepartmentEntity getDepartmentOrThrow(Long departmentId) throws DataProcessException {
        Optional<DepartmentEntity> departmentOpt = departmentRepository.findById(departmentId);
        if(!departmentOpt.isPresent()){
            throw new DataProcessException("Немає такого підрозділу");
        }
        return departmentOpt.get();
    }

    /**
     * Повертає роль за ідентифікатором
     * @param roleId Ідентифікатор ролі
     * @return Роль
     * @throws DataProcessException, якщо такої ролі немає
     */
    public RoleEntity getRoleOrThrow(Long roleId) throws DataProcessException {
        Optional<RoleEntity> roleOpt = roleRepository.findById(roleId);
        if(!roleOpt.isPresent()){
            throw new DataProcessException("Немає такої ролі");
        }
        return roleOpt.get();
    }

    /**
     * Повертає заявку за ідентифікатором
     * @param applicationId Ідентифікатор заявки
     * @return Заявки
     * @throws DataProcessException, якщо такої заявки немає
     */
    public ApplicationEntity getApplicationOrThrow(Long applicationId) throws DataProcessException {
        Optional<ApplicationEntity> applicationOpt = applicationRepository.findById(applicationId);
        if(!applicationOpt.isPresent()){
            throw new DataProcessException("Немає такої заявки");
        }
        return applicationOpt.get();
    }

    /**
     * Метод створений для того, щоб викидати DataProcessException, якщо сервіс не належить власнику
     * @param service Сервіс
     * @param user Потенціальний власник сервісу
     * @throws DataProcessException, якщо сервіс не належить власнику
     */
    public void throwIfServiceNotOfOwner(ServiceEntity service, UserEntity user) throws DataProcessException {

        List<ServiceEntity> ownerServices = serviceRepository.getAllByOwnerUser(user);
        if(!ownerServices.contains(service)){
            throw new DataProcessException("Це не сервіс власника");
        }
    }

    /**
     * Метод створений для того, щоб викидати DataProcessException, якщо сервіс не належить адміністратору
     * @param service Сервіс
     * @param user Потенціальний адміністратор сервісу
     * @throws DataProcessException, якщо сервіс не належить адміністратору
     */
    public void throwIfServiceNotOfAdmin(ServiceEntity service, UserEntity user) throws DataProcessException {

        List<UserHaveAccessToServiceEntity> accesses = accessRepository.getAllByUserAndRole(
                user,
                roleRepository.findById(RoleConstants.ADMIN).get());
        for (UserHaveAccessToServiceEntity access: accesses
             ) {
            if(access.getService().equals(service)){
                return;
            }
        }
        throw new DataProcessException("Це не сервіс адміністратора");
    }

    /**
     * Повертає список заявок, які не перевірялись власником
     * @param allApplications Початковий список заявок, з якого будуть вибиратися потрібні
     * @return Список заявок
     */
    public List<ApplicationEntity> getRefreshedApplicationsForOwner(List<ApplicationEntity> allApplications){

        List<ApplicationEntity> refreshedApplications = new ArrayList<>();

        for (ApplicationEntity app: allApplications) {//дивимося на заявки
            for (ApplicationCheckingEntity check: app.getCheckings() ) {//дивимося на записи заявок
                if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) &&
                        check.getCheckYesNoNull() == null ){//якщо це запис потрібної ролі, і він не перевірений
                    refreshedApplications.add(app);
                    break;
                }
            }
        }
        return refreshedApplications;
    }

    /**
     * Повертає усі заявки, що стосуються адміністратора (заявки по сервісам адміністратора)
     * @param admin Адміністратор
     * @return Список заявок
     */
    public List<ApplicationEntity> getAllApplicationsForAdmin(UserEntity admin){
        List<UserHaveAccessToServiceEntity> accesses = accessRepository.getAllByUserAndRole(admin,
                roleRepository.findById(RoleConstants.ADMIN).get()
        );
        List<ApplicationEntity> applications = new ArrayList<>();
        for (UserHaveAccessToServiceEntity access: accesses) {
            applications.addAll(applicationRepository.getAllByService(access.getService()));
        }
        return applications;
    }

    /**
     * Повертає список заявок, які перевірялись власником, але не перевірялись адміністратором
     * @param allApplications Список заявок, з яких вибираються потрібні
     * @return Список заявок
     */
    public List<ApplicationEntity> getRefreshedApplicationsForAdmin(List<ApplicationEntity> allApplications){

        List<ApplicationEntity> refreshedApplications = new ArrayList<>();

        for (ApplicationEntity app: allApplications) {//дивимося на заявки
            boolean checkedByOwner = false;
            boolean notCheckedByAdmin = false;
            for (ApplicationCheckingEntity check: app.getCheckings() ) {//дивимося на записи заявок
                //спочатку йде перевірка, що включає перевірку на null, бо в іншому випадку може бути NullPointerException
                if (check.getRole().equals(roleRepository.findById(RoleConstants.ADMIN).get()) &&
                        check.getCheckYesNoNull() == null) {//якщо адмін не перевіряв заявку
                    //позначаємо цю заявку
                    notCheckedByAdmin = true;
                } else if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) &&
                        check.getCheckYesNoNull() != null && check.getCheckYesNoNull()) {//якщо власник прийняв заявку
                    //позначаємо цю заявку
                    checkedByOwner = true;
                }
            }
            if(checkedByOwner && notCheckedByAdmin){//якщо заявка перевірена власником і не перевірена адміном
                refreshedApplications.add(app);
            }
        }
        return refreshedApplications;
    }

}
