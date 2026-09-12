package cn.muzisheng.pear.params;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.model.Order;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryValidationTest {

    @Test
    void queryFormRejectsInvalidPosAndLimitButAllowsZeroLimit() {
        QueryForm negativePos = new QueryForm();
        negativePos.setPos(-1);
        assertThrows(IllegalException.class, negativePos::validate);

        QueryForm negativeLimit = new QueryForm();
        negativeLimit.setLimit(-1);
        assertThrows(IllegalException.class, negativeLimit::validate);

        QueryForm excessiveLimit = new QueryForm();
        excessiveLimit.setLimit(Constant.DEFAULT_QUERY_LIMIT + 1);
        assertThrows(IllegalException.class, excessiveLimit::validate);

        QueryForm zeroLimit = new QueryForm();
        zeroLimit.setLimit(0);
        assertDoesNotThrow(zeroLimit::validate);
    }

    @Test
    void queryFormRejectsEmptyCollectionElements() {
        QueryForm nullFilter = new QueryForm();
        nullFilter.setFilters(Collections.singletonList(null));
        assertThrows(IllegalException.class, nullFilter::validate);

        QueryForm nullOrder = new QueryForm();
        nullOrder.setOrders(Collections.singletonList(null));
        assertThrows(IllegalException.class, nullOrder::validate);

        QueryForm blankViewField = new QueryForm();
        blankViewField.setViewFields(List.of("id", " "));
        assertThrows(IllegalException.class, blankViewField::validate);

        QueryForm blankSearchField = new QueryForm();
        blankSearchField.setSearchFields(List.of(""));
        assertThrows(IllegalException.class, blankSearchField::validate);
    }

    @Test
    void filterRejectsMissingNameOperatorInvalidOperatorAndInvalidValues() {
        assertThrows(IllegalException.class, () -> filter(null, Constant.FILTER_OP_EQUAL, "x").validate());
        assertThrows(IllegalException.class, () -> filter("name", null, "x").validate());
        assertThrows(IllegalException.class, () -> filter("name", "contains", "x").validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_BETWEEN, List.of("a")).validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_BETWEEN, List.of("a", "b", "c")).validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_IN, List.of()).validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_NOT_IN, new Object[]{}).validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_LIKE, 123).validate());
        assertThrows(IllegalException.class, () -> filter("name", Constant.FILTER_OP_LIKE, List.of("a", 123)).validate());
    }

    @Test
    void orderRejectsEmptyNameAndInvalidDirection() {
        assertThrows(IllegalException.class, () -> new Order("", Constant.ORDER_OP_ASC).validate());
        assertThrows(IllegalException.class, () -> new Order("id", "sideways").validate());
        assertDoesNotThrow(() -> new Order("id", "DESC").validate());
    }

    private static Filter filter(String name, String op, Object value) {
        Filter filter = new Filter();
        filter.setName(name);
        filter.setOp(op);
        filter.setValue(value);
        return filter;
    }
}
