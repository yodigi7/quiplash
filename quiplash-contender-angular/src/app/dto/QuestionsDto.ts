export class QuestionsDto {
    public id: number;
    public question: string;
    public answer: string;
    
    constructor(fullRestDto: object) {
        // this.answer = fullRestDto.body.answer;
        // this.question = fullRestDto.body.question;
        // this.id = fullRestDto.body.id;
    }
}