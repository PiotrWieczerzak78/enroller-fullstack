<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length == 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
      Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list :meetings="meetings"
                   :username="username"
                   @attend="addMeetingParticipant($event)"
                   @unattend="removeMeetingParticipant($event)"
                   @delete="deleteMeeting($event)"></meetings-list>
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";

    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: []
            };
        },
        methods: {
            addNewMeeting(meeting) {

              this.$http.post('meetings', meeting)
                  .then(response => {
                    console.log("addNewMeeting"+response);
                    // this.refreshMeetings();

                    this.meetings.push(meeting);
                  })
                  .catch(err => console.log("error:" + err));

            },
            addMeetingParticipant(meeting) {
              let myTitle = meeting.title;
              let index;
              console.log("addMeetingParticipant"+' myTitle = ' +myTitle);
              this.$http.get('meetings/getId/'+myTitle)
                  .then((response) => {
                        console.log("addMeetingParticipant"+' meetings/getId '+ response.body);
                        if(response.status == "200"){
                          index = response.body;
                          console.log("addMeetingParticipant"+' index '+ index);
                          this.$http.post('meetings/' + index +'/participant/' + this.username)
                            .then((response) => {
                                console.log("addMeetingParticipant"+response.body);
                                meeting.participants.push(this.username);
                            })
                            .catch(err => console.log("error" + err))
                        }
                  })
                  .catch(err => console.log("error" + err))
            },
            removeMeetingParticipant(meeting) {
              let myTitle = meeting.title;
              let index;
              console.log("removeMeetingParticipant"+' myTitle = ' +myTitle);
              this.$http.get('meetings/getId/'+myTitle)
                  .then((response) => {
                        console.log("removeMeetingParticipant"+' meetings/getId '+ response.body);
                        if(response.status == "200"){
                          index = response.body;
                          console.log("removeMeetingParticipant"+' index '+ index);
                          this.$http.delete('meetings/' + index +'/participant/' + this.username)
                              .then((response) => {
                                console.log("removeMeetingParticipant"+response.body);
                                meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
                              })
                              .catch(err => console.log("error" + err))
                        }
                  })
                  .catch(err => console.log("error" + err))
            },
            deleteMeeting(meeting) {
              let myTitle = meeting.title;
              let index;
              console.log("deleteMeeting"+' myTitle = ' +myTitle);
              this.$http.get('meetings/getId/'+myTitle)
                  .then((response) => {
                    console.log("deleteMeeting"+' meetings/getId '+ response.body);
                        if(response.status == "200"){
                          index = response.body;
                          console.log("deleteMeeting"+' index '+ index);
                          this.$http.delete('meetings/' + index)
                              .then((response) => {
                                console.log(response);
                                this.meetings.splice(this.meetings.indexOf(meeting), 1);
                              })
                              .catch(err => console.log("error" + err))
                        }
                  })
                  .catch(err => console.log("error" + err))
            },
          refreshMeetings() {

            this.$http.get('meetings')
                .then(response => {
                  console.log("refreshMeetings"+response);
                  if(response.status == "200"){
                    this.meetings=response.body;
                  }
                })
                .catch(err => console.log("error" + err))

          }
        },

      mounted() {
        this.refreshMeetings();
      }
    }
</script>
