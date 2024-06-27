package br.com.joaomatos.hubfinanceiro;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager,
            JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(ItemReader<TransactionCNAB> reader,
            ItemProcessor<TransactionCNAB, Transaction> processor,
            ItemWriter<Transaction> writer) {
        return new StepBuilder("step", jobRepository)
                .<TransactionCNAB, Transaction>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

    }

    @Bean
    FlatFileItemReader<TransactionCNAB> reader() {
        return new FlatFileItemReaderBuilder<TransactionCNAB>()
                .name("reader")
                .resource(fileResource())
                .fixedLength()
                .columns(rangeArray())
                .names(
                        "type",
                        "date",
                        "value",
                        "cpf",
                        "card",
                        "hour",
                        "storeOwner",
                        "storeName")
                .targetType(TransactionCNAB.class)
                .build();
    }

    private FileSystemResource fileResource() {
        // Use a property or environment variable for the file path
        String filePath = "files/CNAB.txt";
        return new FileSystemResource(filePath);
    }

    private Range[] rangeArray() {
        return new Range[] {
                new Range(1, 1), new Range(2, 9),
                new Range(10, 19), new Range(20, 30),
                new Range(31, 42), new Range(43, 48),
                new Range(49, 62), new Range(63, 80)
        };
    }

}
