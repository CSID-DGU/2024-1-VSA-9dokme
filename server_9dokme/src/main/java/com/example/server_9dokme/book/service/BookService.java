package com.example.server_9dokme.book.service;

import com.example.server_9dokme.book.dto.response.BookCheckDto;
import com.example.server_9dokme.book.dto.response.BookWebViewDto;
import com.example.server_9dokme.book.entity.Book;
import com.example.server_9dokme.book.repository.BookRepository;
import com.example.server_9dokme.member.dto.response.BookDto;
import com.example.server_9dokme.member.entity.Member;
import com.example.server_9dokme.member.repository.MemberRepository;
import com.example.server_9dokme.rent.entity.Rent;
import com.example.server_9dokme.rent.repository.RentRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Data
@NoArgsConstructor
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private MemberRepository memberRepository;

    public BookCheckDto checkBook(Long bookId,String email){

        Long memberId = memberRepository.findBySocialId(email).getMemberId();
        Book book = bookRepository.findByBookId(bookId);
        Rent rent = rentRepository.findByBookIdAndMemberId(bookId,memberId);


        BookCheckDto dto = BookCheckDto.builder().
                bookId(book.getBookId()).
                title(book.getTitle()).
                author(book.getAuthor()).
                description(book.getDescription()).
                publisher(book.getPublisher()).
                category(book.getCategory()).
                pdfImage(book.getBookImage()).
                lastPage(rent.getLastPage()).
                category(book.getCategory()).build();



            return dto;
    }

    public Page<BookDto> searchBook(String title, int pageNo){

        Pageable pageable = PageRequest.of(pageNo,4);

        Page<Book> bookPage = bookRepository.findByTitleContaining(title,pageable);

        Page<BookDto> bookDtoPage = bookPage.map(book -> new BookDto(
                book.getBookId(),
                book.getTitle(),
                book.getCategory(),
                book.getBookURL(),
                book.getBookImage()));



        return bookDtoPage;
    }


    public BookWebViewDto bookWebView(Long bookId){
        Book book = bookRepository.findByBookId(bookId);




        return BookWebViewDto.builder().
                title(book.getTitle()).
                category(book.getCategory()).
                author(book.getAuthor()).
                pdfUrl(book.getBookURL()).
                build();
    }


    public void saveRentBook(Long bookId, String socialId){


        Member member = memberRepository.findBySocialId(socialId);


        if(!rentRepository.existsByBookId(bookId)){
            Rent initRent = new Rent();
            initRent.setBookId(bookId);
            initRent.setProgress(0L);
            initRent.setRentDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            initRent.setMemberId(member.getMemberId());
            initRent.setReadAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

            rentRepository.save(initRent);
        }else{
            Rent updateRent = rentRepository.findByBookId(bookId);
            updateRent.setReadAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

            rentRepository.save(updateRent);
        }

    }

    public void updateProgress(Long bookId, String email, int lastPage){

        Book book = bookRepository.findByBookId(bookId);

        Long memberId = memberRepository.findBySocialId(email).getMemberId();

        Rent updateRent = rentRepository.findByBookIdAndMemberId(bookId,memberId);

        int fullPage = book.getBookFullPage();

        float progress = ((float)lastPage /fullPage) * 100;

        log.info("progress:{}",progress);
        log.info("lastPage:{}",lastPage);
        log.info("fullPage:{}",fullPage);


        updateRent.setProgress(progress);
        updateRent.setLastPage(lastPage);

        rentRepository.save(updateRent);
    }


}
