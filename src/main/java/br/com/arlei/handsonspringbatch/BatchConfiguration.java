package br.com.arlei.handsonspringbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    // Criando um JOb o bean faz a injeção do JobRepository as implemntações que são feitas para os beans começam coim simple
    // Um JOb pode ter n Step de todos tipos: chunck ou tasklest
    // passar no parametrtro ou no @Bean
    // .next estamos executando de forma ordenada..

    @Bean
    public Job processarPessoa(JobRepository jobRepository,Step step, Step step2){


        // Passar nome do JOB e JOB Repository ai o Spring Batch ja cria
        return new JobBuilder("importacaoPessoa",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step).next(step2).build();

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
        // neste ponto executou um processo sincrno java -jar nome do nosso jar.
        // mas alguns dizem que se chamarmos um metodo assincrino ela vai ate mais rapido.
        // Teste taskExecutor(new SimpleAsyncTaskExecutor)

         return new StepBuilder("step01",jobRepository)
                 .<Pessoa,Pessoa>chunk(14,platformTransactionManager)
                 .reader(itemReader)
                 .writer(itemWriter)
                 .taskExecutor(new SimpleAsyncTaskExecutor())
                 .processor(itemProcessor).build();

    }

    // Tasklet algo mais simples que um chunk que e para processamento mais pesado

    @Bean
    public Step step2(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager,
                      Tasklet takslet){

        return new StepBuilder("Log4fun",jobRepository)
                .tasklet(takslet,platformTransactionManager)
                .build();

    }

    @Bean
    public Tasklet tasklet(){

        return  (contribution, chunkContext) -> {
            System.out.println("Vamos esperar 30 segundos");
            Thread.sleep(10000);
            return RepeatStatus.FINISHED;
            // se colocar para continuar a execução vai ocorrendo como um loop

        };

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
        /// NÃO USAMOS JÁ IMPLEMenTaÇÕES PRONTAS, POIS TEM A HAVER COM NOSSA REGRA

        return new PessoaProcessor();
    }

}
