package net.lightapi.portal.form;

import java.io.IOException;

public interface FormRepository {

    void setDataSource(Object dataSource);

    Object getDataSource();

    String getForm();

    String getFormById(String id);

    String getFormByEntityId(String id);

    void createForm(String data);

    void updateForm(String data);

    void removeForm(String data);
}
