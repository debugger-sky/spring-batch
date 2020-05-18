package com.sky.springbatch.reader;

import com.sky.springbatch.repository.EmNdmsDailyRepository;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NdmsDailyItemReader<T> extends RepositoryItemReader {

    private final int pageSize = 2;
    private PagingAndSortingRepository<?, ?> repository;
    private String methodName = "findByAge";
    private Sort sort = Sort.by(Sort.Direction.ASC, "id");
    private List<Integer> arguments = new ArrayList<Integer>();

    public NdmsDailyItemReader(EmNdmsDailyRepository emNdmsDailyRepository) {
        arguments.add(26);
        repository = emNdmsDailyRepository;
    }

    @Override
    protected List doPageRead() throws Exception {
        Pageable pageRequest = PageRequest.of(0, pageSize, sort);

        MethodInvoker invoker = createMethodInvoker(repository, methodName);

        List<Object> parameters = new ArrayList<>();

        if(arguments != null && arguments.size() > 0) {
            parameters.addAll(arguments);
        }

        parameters.add(pageRequest);

        invoker.setArguments(parameters.toArray());

        Page<T> curPage = (Page<T>) doInvoke(invoker);

        return curPage.getContent();
    }

    private Object doInvoke(MethodInvoker invoker) throws Exception {
        try {
            invoker.prepare();
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            else {
                throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
            }
        }
        catch (IllegalAccessException e) {
            throw new DynamicMethodInvocationException(e);
        }
    }

    private MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        return invoker;
    }
}
