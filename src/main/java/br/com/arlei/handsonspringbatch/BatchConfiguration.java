package br.com.arlei.handsonspringbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    // Criando um JOb o bean faz a injeção do JobRepository as implemntações que são feitas para os beans começam coim simple
    @Bean
    public Job processarPessoa(JobRepository jobRepository,Step step){


        // Passar nome do JOB e JOB Repository ai o Spring Batch ja cria
        return new JobBuilder("importacaoPessoa",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step).build();

    }

    @Bean
    // encontramos estes beans ja prontos pelo proprio Spring Batch
    public Step step(JobRepository jobRepository
            ,PlatformTransactionManager platformTransactionManager
            ,ItemReader<Pessoa> itemReader
            ,ItemWriter<Pessoa> itemWriter
            ,ItemProcessor<Pessoa,Pessoa> itemProcessor
            ){

        // temos tasklet se usasse um ftp para trazer o arquivo e chunck 2 opções
         return new StepBuilder("step01",jobRepository)
                 .<Pessoa,Pessoa>chunk(14,platformTransactionManager)
                 .reader(itemReader)
                 .writer(itemWriter)
                 .processor(itemProcessor).build();

    }

    // se fosse usar generico para varias parametros, não seria bean e chamaria o metodo para cada arquivo
    @Bean
    public ItemReader<Pessoa> personItemReader(){

        BeanWrapperFieldSetMapper<Pessoa> fieldSetMapper
                = new BeanWrapperFieldSetMapper<>();

        fieldSetMapper.setTargetType(Pessoa.class);

        // Jas tem varias implementações prontas mas caso nenhuma se encaixe podemos criar as nossas
        return new FlatFileItemReaderBuilder<Pessoa>()
                .name("pessoaItemReader")
                .resource( new ClassPathResource("pessoas.csv") )
                .delimited()
                .names("nome","endereco","bairro","cidade","estado")
                .fieldSetMapper(fieldSetMapper).build();
    }

    // Ja vem do Spring Batch pegando nossa conexão
    @Bean
    public ItemWriter<Pessoa> personItemWriter(DataSource dataSource){

        return new JdbcBatchItemWriterBuilder<Pessoa>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql("insert into PESSOA "
                + "(nome,endereco,bairro,cidade,estado,create_date_time) "
                + "VALUES (:nome, :endereco, :bairro, :cidade, :estado, :create_date_time) ")
        .build();

    }

    @Bean
    public ItemProcessor<Pessoa,Pessoa> itemProcessor(){
        /// NÃO USAMOS JÁ IMPLEMNATÇÕES PRONTAS, POIS TEM A HAVER COM NOSSA REGRA

        return new PessoaProcessor();
    }

}
