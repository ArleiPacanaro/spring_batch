package br.com.arlei.handsonspringbatch;

import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

public class PessoaProcessor implements ItemProcessor<Pessoa,Pessoa> {


    @Override
    public Pessoa process(Pessoa item) throws Exception {
        item.setCreate_date_time(LocalDateTime.now());
        return item;
    }
}
