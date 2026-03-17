package com.dkt.bai2webbanhangtruong.pagination;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;

public class PaginationResult<E> {

    private int totalRecords;
    private int currentPage;
    private List<E> list;
    private int maxResult;
    private int totalPages;
    private int maxNavigationPage;
    private List<Integer> navigationPages;

    public PaginationResult(Page<E> page, int maxNavigationPage) {
        this.totalRecords = (int) page.getTotalElements();
        this.currentPage = page.getNumber() + 1;
        this.list = page.getContent();
        this.maxResult = page.getSize();
        this.totalPages = page.getTotalPages();
        this.maxNavigationPage = maxNavigationPage;
        this.calcNavigationPages();
    }

    @SuppressWarnings({"deprecation", "removal"})
    public PaginationResult(Query<E> query, int page, int maxResult, int maxNavigationPage) {
        final int pageIndex = Math.max(page - 1, 0);
        int fromRecordIndex = pageIndex * maxResult;

        try (ScrollableResults<E> resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE)) {
            resultScroll.last();
            int rowCount = resultScroll.getRowNumber();
            this.totalRecords = rowCount >= 0 ? rowCount + 1 : 0;

            resultScroll.beforeFirst();
            for (int i = 0; i < fromRecordIndex; i++) {
                if (!resultScroll.next()) break;
            }

            List<E> results = new ArrayList<>();
            while (results.size() < maxResult && resultScroll.next()) {
                E record = resultScroll.get();
                results.add(record);
            }

            this.list = results;
            this.currentPage = pageIndex + 1;
            this.maxResult = maxResult;
            this.totalPages = (int) Math.ceil((double) this.totalRecords / this.maxResult);
        } catch (Exception e) {
            this.list = new ArrayList<>();
            this.totalPages = 0;
        }

        this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
        this.calcNavigationPages();
    }

    private void calcNavigationPages() {
        this.navigationPages = new ArrayList<>();
        int current = Math.min(this.currentPage, this.totalPages);
        if (this.totalPages <= 0) return;

        int begin = current - this.maxNavigationPage / 2;
        int end = current + this.maxNavigationPage / 2;

        navigationPages.add(1);
        if (begin > 2) {
            navigationPages.add(-1);
        }

        for (int i = begin; i <= end; i++) {
            if (i > 1 && i < this.totalPages) {
                navigationPages.add(i);
            }
        }

        if (end < this.totalPages - 1) {
            navigationPages.add(-1);
        }

        if (this.totalPages > 1) {
            navigationPages.add(this.totalPages);
        }
    }

    // Getters
    public int getTotalRecords() { return totalRecords; }
    public int getCurrentPage() { return currentPage; }
    public List<E> getList() { return list; }
    public int getMaxResult() { return maxResult; }
    public int getTotalPages() { return totalPages; }
    public List<Integer> getNavigationPages() { return navigationPages; }
}