/*
 * Copyright (c) 2012,  All rights reserved.
 */
package org.zenframework.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zenframework.util.URLUtils;

/**
 * Value object used in pagination
 * @author Zed
 */
public class Pagination<T> implements Serializable {
	
	/**
	 * Attribute key for request or session
	 */
	public static final String ATTR_KEY = "pagination";
	
	/**
	 * Current page number
	 */
	public static final String PAGE_NO = "pageNo";
	
	/**
	 * Max page
	 */
	public static final String PAGE_SIZE = "pageSize";
	
	/**
	 * Sorting key
	 */
	public static final String SORT_KEY = "sortKey";
	
	/**
	 * Sort by asc
	 */
	public static final String SORT_ASC = "sortAsc";
	
	/**
	 * Max record count per page
	 */
	public static final int MAX_PAGE_SIZE = 100;

	/**
	 * Default record count per page
	 */
	public static final int DEFALUT_PAGE_SIZE = 10; 

	/** SUID */
	private static final long serialVersionUID = 5738703782174909934L;
	
	/** Number of record  per page */
	private int numPerPage = DEFALUT_PAGE_SIZE;
	
	/** Current page number */
    private int pageNum = 1;
    
    /** Total page */
    private int totalPage;
    
    /** Total record count */
    private int totalCount;
    
    /** Parameter that's used in sql query */
    private Object param = null;
    
    /** Sorted key or label from request and it's not column name */
    private String sortKey;
    
    /**
	 * Sorted column name which is converted from sortKey and use it to generate sql
	 * @deprecated
	 */
    private String sortColumn;

    /** Sort by key asc or desc */
	private boolean sortAsc;
	
	/** Record list of current page */
	private List<T> records = null;
	
	/** Request parameter map*/
	private Map<String, String> requestParamMap = null;
	
	/** Pagination request uri that excluded parameters */
	private String uri = "";
    
    public Pagination() {
    }

    /**
     * 
     * @param numPerPage
     */
    public Pagination(int numPerPage) {
    	this(1, numPerPage);
    }

    /**
     * 
     * @param currentPage
	 * @param numPerPage
     */
    public Pagination(int currentPage, int numPerPage) {
        this.pageNum = currentPage;
        this.numPerPage = numPerPage;
    }
    
    /**
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * @param pageNum the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        int p = this.totalCount / this.numPerPage;
        int r = this.totalCount % this.numPerPage;
        this.totalPage = r > 0 ? p + 1 : p;
        //this.pageNum = this.totalPage < this.pageNum ? 1 : this.pageNum;
    }

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}
	
	public int getStartIndex() {
		return (this.pageNum - 1) * this.numPerPage;
	}
	
	public int getEndIndex() {
		return this.pageNum  * this.numPerPage - 1;
	}
	
	public boolean isHavingNextPage() {
		return this.totalPage > 0 && this.pageNum < this.totalPage;
	}
	
	public boolean isHavingPreviousPage() {
		return this.totalPage > 0 && this.pageNum > 1;
	}

	/**
	 * @param sortKey the sortKey to set
	 */
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * @return the sortAsc
	 */
	public boolean isSortAsc() {
		return sortAsc;
	}

	/**
	 * @param sortAsc the sortAsc to set
	 */
	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	/**
	 * @return the records
	 */
	public List<T> getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}

	/**
	 * @return the sortColumn
	 */
	public String getSortColumn() {
		return sortColumn;
	}
	
	/**
	 * Tranfer the sort key to column, make sure to call it if request contains sortKey
	 * @param mapping
	 * @deprecated Will delete it in the future
	 */
	public void sortKeyToColumn(Map<String, String> mapping) {
		if (mapping == null || mapping.size() <= 0 || this.sortKey == null) {
			return;
		}
		String _sortColumn = mapping.get(this.sortKey);
		if (_sortColumn != null) {
			this.sortColumn = _sortColumn;
		}
	}

	/**
	 * @return the requestParamMap
	 * @deprecated Use getRequestParamMap
	 */
	public Map<String, String> getParamMap() {
		return getRequestParamMap();
	}

	/**
	 * @param paramMap the requestParamMap to set
	 * @deprecated Use setRequestParamMap instead
	 */
	public void setParamMap(Map<String, String> paramMap) {
		setRequestParamMap(paramMap);
	}

	/**
	 * @return the requestParamMap
	 */
	public Map<String, String> getRequestParamMap() {
		return requestParamMap;
	}

	/**
	 * @param paramMap the requestParamMap to set
	 */
	public void setRequestParamMap(Map<String, String> paramMap) {
		this.requestParamMap = paramMap;
		if (this.requestParamMap != null) {
			this.requestParamMap.remove(Pagination.PAGE_NO);
		}
	}
	
	public Pagination<T> getNextPage() {
		if (!this.isHavingNextPage()) {
			return null;
		}
		Pagination<T> page = new Pagination<T>();
		page.setPageNum(this.pageNum + 1);
		page.setUri(this.uri);
		page.setRequestParamMap(this.requestParamMap);
		return page;
	}
	
	public Pagination<T> getPreviousPage() {
		if (!this.isHavingPreviousPage()) {
			return null;
		}
		Pagination<T> page = new Pagination<T>();
		page.setPageNum(this.pageNum - 1);
		page.setUri(this.uri);
		page.setRequestParamMap(this.requestParamMap);
		return page;
	}

	/**
	 * FIXME Try to improve it when having time
	 * @return
	 */
	public List<Pagination<T>> getVisualPages() {
		if (this.totalCount <= 0) {
			return null;
		}
		List<Pagination<T>> pages = new ArrayList<>(11);
		int minPage = this.pageNum - 2;
		if (minPage < 1) {
			minPage = 1;
		}
		int maxPage = this.pageNum + 2;
		if (maxPage > this.totalPage) {
			maxPage = this.totalPage;
		}
		for (int i = 1; i <= 2; ++i) {
		    if (i > this.totalPage) {
		    	break;
		    }
		    Pagination<T> page = new Pagination<T>();
		    page.setPageNum(i);
		    page.setUri(this.uri);
		    page.setRequestParamMap(this.requestParamMap);
		    pages.add(page);
		}
		//[...]
		if (minPage >= 5) {
			pages.add(null);
		}
		for (int i = minPage; i <= maxPage; ++i) {
			if (i == 1 || i == 2) {
				continue;
			}
		    Pagination<T> page = new Pagination<T>();
		    page.setPageNum(i);
		    page.setUri(this.uri);
		    page.setRequestParamMap(this.requestParamMap);
		    pages.add(page);
		}
		//[...]
		if (maxPage < this.totalPage) {
			pages.add(null);
		}
		return pages;
	}
	

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Get pagination url
	 * @return
	 */
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.uri);
		String queryString = "";
		if (this.requestParamMap != null && this.requestParamMap.size() > 0) {
			queryString = URLUtils.getURLQueryString(this.requestParamMap, true);
		}
		if (this.uri.indexOf('?') == -1) {
			sb.append('?');
		}
		sb.append(Pagination.PAGE_NO).append('=').append(this.pageNum);
		if (queryString.length() > 0) {
			sb.append('&').append(queryString);
		}
		return sb.toString();
	}
	
	/**
	 * Get pagination url
	 * @return
	 */
	public String getUrlWithoutPageNum() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.uri);
		String queryString = "";
		if (this.requestParamMap != null && this.requestParamMap.size() > 0) {
			queryString = URLUtils.getURLQueryString(this.requestParamMap, true);
		}

		if (queryString.length() > 0) {
			if (this.uri.indexOf('?') == -1) {
				sb.append('?').append(queryString);
			}
			else {
				sb.append('&').append(queryString);	
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <C> Pagination<C> cast(Class<C> clazz) {
		return (Pagination<C>)this;
	}
}

